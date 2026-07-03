package com.ecommerce.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Central Redis configuration shared across all microservices.
 * Provides:
 *  - Lettuce connection pool
 *  - JSON serialization (Jackson) with Java 8 time support
 *  - Per-cache TTL configuration
 *  - Spring @Cacheable/@CacheEvict support via CacheManager
 */
@Configuration
@EnableCaching
public class RedisConfig {

    // ── Connection Pool ──────────────────────────────────────────────────────────

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public LettuceConnectionFactory redisConnectionFactory(
            RedisProperties props,
            ClientResources clientResources) {

        RedisStandaloneConfiguration standaloneConfig =
                new RedisStandaloneConfiguration(props.getHost(), props.getPort());
        if (props.getPassword() != null && !props.getPassword().isBlank()) {
            standaloneConfig.setPassword(props.getPassword());
        }
        standaloneConfig.setDatabase(props.getDatabase());

        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(props.getPool().getMaxActive());
        poolConfig.setMaxIdle(props.getPool().getMaxIdle());
        poolConfig.setMinIdle(props.getPool().getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(props.getPool().getMaxWait()));
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        LettucePoolingClientConfiguration clientConfig =
                LettucePoolingClientConfiguration.builder()
                        .poolConfig(poolConfig)
                        .clientResources(clientResources)
                        .commandTimeout(Duration.ofSeconds(5))
                        .build();

        LettuceConnectionFactory factory =
                new LettuceConnectionFactory(standaloneConfig, clientConfig);
        factory.setValidateConnection(true);
        return factory;
    }

    // ── Serialization ────────────────────────────────────────────────────────────

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Embed type info so polymorphic types deserialize correctly
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer redisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        template.setEnableTransactionSupport(false); // enable per-service if needed
        template.afterPropertiesSet();
        return template;
    }

    // ── Cache Manager ────────────────────────────────────────────────────────────

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer redisSerializer) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));

        // Per-cache TTL overrides
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put(CacheNames.OTP,           defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put(CacheNames.JWT_BLACKLIST,  defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigs.put(CacheNames.USER_PROFILE,  defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put(CacheNames.PRODUCT,        defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put(CacheNames.CART,           defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigs.put(CacheNames.ORDER,          defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put(CacheNames.INVENTORY,      defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put(CacheNames.RATE_LIMIT,     defaultConfig.entryTtl(Duration.ofSeconds(60)));
        cacheConfigs.put(CacheNames.SESSION,        defaultConfig.entryTtl(Duration.ofHours(8)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}
