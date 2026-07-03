package com.ecommerce.redis.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Runs a SET/GET/DEL smoke-test after the Spring context is fully started.
 * Fails fast with a clear error if Redis is unreachable.
 */
@Component
public class RedisStartupVerifier {

    private static final Logger log = LoggerFactory.getLogger(RedisStartupVerifier.class);
    private static final String TEST_KEY = "startup:ping:" + UUID.randomUUID();

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStartupVerifier(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verifyRedisOnStartup() {
        try {
            redisTemplate.opsForValue().set(TEST_KEY, "ok");
            Object value = redisTemplate.opsForValue().get(TEST_KEY);
            redisTemplate.delete(TEST_KEY);

            if ("ok".equals(value)) {
                log.info("✅ Redis connectivity verified — SET/GET/DEL smoke-test passed.");
            } else {
                log.error("❌ Redis smoke-test returned unexpected value: {}", value);
                throw new IllegalStateException("Redis smoke-test failed: unexpected value.");
            }
        } catch (Exception ex) {
            log.error("❌ Redis is UNREACHABLE at startup. Check docker-compose or SPRING_REDIS_HOST env var.", ex);
            throw new IllegalStateException("Redis startup verification failed.", ex);
        }
    }
}
