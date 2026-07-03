# 🛒 E-Commerce Microservices Backend

> Production-ready Java Spring Boot Microservices Architecture

## 📦 Services

| Service | Port | Description |
|---|---|---|
| API Gateway | 8080 | Central entry point, JWT validation, routing |
| Auth Service | 8081 | JWT, Refresh Token, Email Verification, OTP, RBAC |
| User Service | 8082 | Profile, Address, Wallet, Preferences |
| Merchant Service | 8083 | Store, Products, Categories, Inventory, Offers, Reviews |
| Order Service | 8084 | Cart, Checkout, Coupon, Invoice, Refund, Cancellation |
| Delivery Service | 8085 | Rider Assignment, Tracking, ETA, OTP Delivery |
| Payment Service | 8086 | Razorpay, Stripe, COD, Wallet |
| Notification Service | 8087 | Email, SMS, Push, WhatsApp |
| Analytics Service | 8088 | Revenue, Orders, Customers, Merchants |

## 🏗️ Architecture

```
Client → API Gateway (8080)
            ↓
    ┌───────────────────────────────────┐
    │         Microservices             │
    │  Auth │ User │ Merchant │ Order   │
    │  Delivery │ Payment │ Notify │ Analytics │
    └───────────────────────────────────┘
            ↓
    Infrastructure: Eureka + Kafka + Redis + PostgreSQL
```

## 🚀 Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** + **JWT (jjwt 0.12)**
- **Spring Cloud Gateway** + **Eureka Service Discovery**
- **Apache Kafka** – async events
- **Redis** – caching & OTP store
- **PostgreSQL** – primary DB
- **Razorpay & Stripe** – payments
- **Firebase** – push notifications
- **Twilio** – SMS/WhatsApp
- **JavaMailSender** – email
- **Swagger/OpenAPI 3** – API docs
- **JUnit 5 + Mockito** – unit tests
- **Docker Compose** – local orchestration

## ▶️ Running Locally

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Start each service (from its directory)
./mvnw spring-boot:run
```

## 📖 API Documentation

Each service exposes Swagger UI at `/swagger-ui.html`

- Auth: http://localhost:8081/swagger-ui.html
- User: http://localhost:8082/swagger-ui.html
- Merchant: http://localhost:8083/swagger-ui.html
- Order: http://localhost:8084/swagger-ui.html
- Delivery: http://localhost:8085/swagger-ui.html
- Payment: http://localhost:8086/swagger-ui.html
- Notification: http://localhost:8087/swagger-ui.html
- Analytics: http://localhost:8088/swagger-ui.html
