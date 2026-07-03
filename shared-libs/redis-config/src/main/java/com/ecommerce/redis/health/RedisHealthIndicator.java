package com.ecommerce.redis.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Exposes Redis health at /actuator/health.
 * A PING → PONG confirms connectivity on startup and ongoing checks.
 */
@Component
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory connectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        try (var conn = connectionFactory.getConnection()) {
            String pong = conn.ping();
            return Health.up()
                    .withDetail("ping", pong)
                    .withDetail("server", conn.serverCommands().info("server"))
                    .build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
