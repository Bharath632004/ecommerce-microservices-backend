# Docker Environment Variables for Redis Connectivity

When running services inside Docker Compose, Spring Boot must resolve
`redis` (the Docker service name) instead of `localhost`.

Add the following environment block to **each microservice** in `docker-compose.yml`:

```yaml
environment:
  SPRING_REDIS_HOST: redis
  SPRING_REDIS_PORT: 6379
  # Optional if Redis has a password:
  # SPRING_REDIS_PASSWORD: yourpassword
```

Also add the `depends_on` guard:

```yaml
depends_on:
  redis:
    condition: service_healthy
```

## Service-specific cache key prefixes (auto-set via spring.application.name)

| Service           | Key prefix             | Example key                      |
|-------------------|------------------------|----------------------------------|
| auth-service      | `auth-service:`        | `auth-service:otp:9876543210`    |
| user-service      | `user-service:`        | `user-service:user:profile:42`   |
| merchant-service  | `merchant-service:`    | `merchant-service:product:101`   |
| order-service     | `order-service:`       | `order-service:cart:userId:7`    |
| delivery-service  | `delivery-service:`    | `delivery-service:tracking:ord9` |
