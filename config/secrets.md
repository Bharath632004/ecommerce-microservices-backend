# Secrets & Configuration Reference

This project uses **environment variables only** — no hardcoded secrets anywhere.
All configuration is injected at runtime via `.env` (local) or CI/CD secrets (production).

## Variable Reference

| Variable | Used By | Description |
|---|---|---|
| `POSTGRES_USER` | All services, docker-compose | PostgreSQL username |
| `POSTGRES_PASSWORD` | All services, docker-compose | PostgreSQL password |
| `POSTGRES_DB` | All services, docker-compose | PostgreSQL database name |
| `POSTGRES_PORT` | docker-compose | Host port mapped to Postgres (default: 5432) |
| `REDIS_HOST` | Local dev only | Redis host (overridden to `redis` in Docker) |
| `REDIS_PORT` | All services | Redis port (default: 6379) |
| `REDIS_PASSWORD` | All services | Redis auth password (empty = no auth) |
| `KAFKA_BOOTSTRAP_SERVERS` | Local dev | Kafka bootstrap (default: localhost:9092) |
| `KAFKA_INTERNAL_BOOTSTRAP_SERVERS` | docker-compose | Internal Docker Kafka address |
| `JWT_SECRET` | auth, user, merchant, order, delivery | JWT signing key (min 32 chars) |
| `JWT_EXPIRATION_MS` | All services | Access token TTL in ms (default: 86400000 = 1 day) |
| `JWT_REFRESH_EXPIRATION_MS` | auth-service | Refresh token TTL in ms (default: 604800000 = 7 days) |
| `EUREKA_PORT` | eureka-server | Eureka dashboard port (default: 8761) |
| `EUREKA_SERVER_URL` | All app services | Eureka registration URL (Docker) |
| `AUTH_SERVICE_PORT` | docker-compose | Host port for auth-service (default: 8081) |
| `USER_SERVICE_PORT` | docker-compose | Host port for user-service (default: 8082) |
| `MERCHANT_SERVICE_PORT` | docker-compose | Host port for merchant-service (default: 8083) |
| `ORDER_SERVICE_PORT` | docker-compose | Host port for order-service (default: 8084) |
| `DELIVERY_SERVICE_PORT` | docker-compose | Host port for delivery-service (default: 8085) |
| `RAZORPAY_KEY_ID` | auth, merchant, order | Razorpay API Key ID |
| `RAZORPAY_KEY_SECRET` | auth, merchant, order | Razorpay API Key Secret |
| `STRIPE_SECRET_KEY` | auth, merchant, order | Stripe Secret Key |
| `STRIPE_PUBLIC_KEY` | auth, merchant, order | Stripe Publishable Key |
| `MAIL_HOST` | auth-service | SMTP host (default: smtp.gmail.com) |
| `MAIL_PORT` | auth-service | SMTP port (default: 587) |
| `MAIL_USERNAME` | auth-service | SMTP sender email |
| `MAIL_PASSWORD` | auth-service | SMTP password / App password |

## Usage

```bash
# 1. Copy the example file
cp .env.example .env

# 2. Fill in your actual values in .env

# 3. Start services
docker compose up --build
```

## Production

In production (CI/CD), set all variables as **environment secrets** in:
- GitHub Actions → Repository → Settings → Secrets and Variables
- AWS Secrets Manager / GCP Secret Manager / Azure Key Vault

Never commit `.env` to git. It is listed in `.gitignore`.
