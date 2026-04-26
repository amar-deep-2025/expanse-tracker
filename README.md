# 💰 Expense Tracker — Backend

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
</p>

A production-ready, full-stack **Expense Tracker REST API** built with **Spring Boot 3** and **Java 21**. It supports JWT-based authentication with OTP verification, budget management, dashboard analytics, multi-format report downloads, Razorpay payment integration, and multi-channel notifications via email and SMS.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture Overview](#-architecture-overview)
- [Project Structure](#-project-structure)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Run Locally](#run-locally)
  - [Run with Docker](#run-with-docker)
- [Configuration Profiles](#-configuration-profiles)
- [Design Patterns Used](#-design-patterns-used)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure stateless auth with access tokens and OTP-based registration
- 📊 **Dashboard Analytics** — Summary stats, category breakdown, monthly trends, and month-over-month comparison
- 💸 **Expense Management** — Full CRUD for expenses with income/expense type classification
- 🎯 **Budget Management** — Create and track budgets (daily, monthly, yearly) per category
- 📥 **Report Downloads** — Export reports as **Excel** or **PDF** in Summary, Category, Full, Monthly, Yearly, and Budget formats
- 💳 **Razorpay Integration** — Create payment orders and verify transactions
- 📧 **OTP Notifications** — Email (JavaMail) and SMS (Twilio) notification strategies
- ⏰ **Scheduled Notifications** — Budget breach alerts via Spring Scheduler
- 🖼️ **Profile Image Upload** — Multipart file upload for user avatars
- 🛡️ **Global Exception Handling** — Consistent error responses across all endpoints
- ⚡ **Redis Caching** — OTP storage and session optimization

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | MySQL 8 (production), H2 (testing) |
| ORM | Spring Data JPA / Hibernate |
| Caching | Redis |
| Notifications | JavaMail (SMTP) + Twilio SMS |
| Payments | Razorpay Java SDK |
| Reports | Apache POI (Excel), iText/custom (PDF) |
| Build | Maven |
| Containerization | Docker (multi-stage build) |

---

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                  Client (React.js)               │
└─────────────────────┬───────────────────────────┘
                      │ HTTP / REST
┌─────────────────────▼───────────────────────────┐
│              Spring Boot Application             │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐  │
│  │ JWT Auth │  │ Security │  │ CORS Config   │  │
│  └──────────┘  └──────────┘  └───────────────┘  │
│  ┌─────────────────────────────────────────────┐ │
│  │              Controllers (REST)              │ │
│  │  Auth | User | Expense | Budget | Dashboard  │ │
│  │       Report Download | Payment              │ │
│  └──────────────────┬──────────────────────────┘ │
│  ┌──────────────────▼──────────────────────────┐ │
│  │              Service Layer                   │ │
│  │  Strategy Pattern (Reports / Notifications)  │ │
│  │  Factory Pattern (Notifications)             │ │
│  └──────────────────┬──────────────────────────┘ │
│  ┌──────────────────▼──────────────────────────┐ │
│  │           Repository (Spring Data JPA)       │ │
│  └──────────────────┬──────────────────────────┘ │
└─────────────────────┼───────────────────────────┘
           ┌──────────┴──────────┐
     ┌─────▼─────┐         ┌─────▼─────┐
     │   MySQL   │         │   Redis   │
     └───────────┘         └───────────┘
```

---

## 📁 Project Structure

```
expanse-tracker-backend/
├── src/main/java/com/amar/fullstack/expanse_tracker_backend/
│   ├── config/                    # Security, JWT, CORS, Redis, Razorpay config
│   │   ├── CorsConfig.java
│   │   ├── JwtFilter.java
│   │   ├── JwtUtil.java
│   │   ├── RazorpayConfig.java
│   │   ├── RedisConfig.java
│   │   └── SecurityConfig.java
│   │
│   ├── controller/                # REST API endpoints
│   │   ├── AuthController.java
│   │   ├── BudgetController.java
│   │   ├── DashboardController.java
│   │   ├── DownloadController.java
│   │   ├── ExpanseController.java
│   │   ├── PaymentController.java
│   │   └── UserController.java
│   │
│   ├── dtos/                      # Data Transfer Objects (request/response)
│   ├── entity/                    # JPA Entities
│   │   ├── User.java
│   │   ├── Expanse.java
│   │   ├── Budget.java
│   │   ├── Payment.java
│   │   └── ExpanseCategory.java
│   │
│   ├── exception/                 # Custom exceptions + global handler
│   ├── mapping/                   # Entity ↔ DTO mappers
│   ├── notification/              # Notification system
│   │   ├── factory/               # NotificationFactory
│   │   ├── scheduler/             # Budget alert scheduler
│   │   ├── service/               # NotificationService
│   │   └── strategy/             # Email & SMS strategies
│   │
│   ├── repository/                # Spring Data JPA repositories
│   ├── service/                   # Business logic
│   │   ├── AuthService.java
│   │   ├── BudgetService.java
│   │   ├── DashboardService.java
│   │   ├── ExpanseService.java
│   │   ├── OtpService.java
│   │   ├── PaymentService.java
│   │   ├── ReportService.java
│   │   └── UserService.java
│   │
│   └── strategy/                  # Report generation strategies
│       ├── ReportStrategy.java    (interface)
│       ├── SummaryReportStrategy.java
│       ├── CategoryReportStrategy.java
│       ├── FullReportStrategy.java
│       ├── MonthlyReportStrategy.java
│       ├── BudgetReportStrategy.java
│       └── ReportStrategyFactory.java
│
├── src/main/resources/
│   ├── application.properties     # Production config (env vars)
│   ├── application-dev.properties # Dev config
│   └── application-test.properties
│
├── Dockerfile                     # Multi-stage Docker build
└── pom.xml
```

---

## 📡 API Reference

> All protected endpoints require the `Authorization: Bearer <token>` header.

### 🔐 Auth — `/api/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/register` | ❌ | Register user — sends OTP to email |
| `POST` | `/verify-otp` | ❌ | Verify OTP to complete registration |
| `POST` | `/login` | ❌ | Login and receive JWT token |
| `POST` | `/forgot-password` | ❌ | Request password reset token |
| `POST` | `/reset-password` | ❌ | Reset password using token |
| `POST` | `/logout` | ✅ | Logout (client discards token) |

---

### 👤 Users — `/api/users`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/me` | ✅ | Get current authenticated user |
| `PUT` | `/me` | ✅ | Update profile (name, etc.) |
| `DELETE` | `/me` | ✅ | Delete account (requires password) |
| `PATCH` | `/me/change-email` | ✅ | Initiate email change — sends OTP |
| `PATCH` | `/me/verify-email` | ✅ | Verify OTP to confirm email change |
| `PATCH` | `/me/change-password` | ✅ | Initiate password change — sends OTP |
| `PATCH` | `/me/verify-password` | ✅ | Verify OTP to confirm new password |
| `POST` | `/upload-image` | ✅ | Upload profile avatar (multipart) |
| `GET` | `/` | ✅ | Get all users (admin) |
| `GET` | `/{id}` | ✅ | Get user by ID |
| `PATCH` | `/{id}/role` | ✅ | Update user role (admin) |

---

### 💸 Expenses — `/api/expanses`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/` | ✅ | Create a new expense/income |
| `GET` | `/` | ✅ | Get all expenses for current user |
| `GET` | `/{id}` | ✅ | Get expense by ID |
| `PUT` | `/{id}` | ✅ | Update expense |
| `DELETE` | `/{id}` | ✅ | Delete expense |

---

### 🎯 Budgets — `/api/budgets`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/` | ✅ | Create a new budget |
| `GET` | `/` | ✅ | Get all budgets for current user |
| `GET` | `/{id}` | ✅ | Get budget by ID |
| `PUT` | `/{id}` | ✅ | Update budget |
| `DELETE` | `/{id}` | ✅ | Delete budget |

---

### 📊 Dashboard — `/api/dashboard`

| Method | Endpoint | Auth | Query Params | Description |
|--------|----------|------|-------------|-------------|
| `GET` | `/summary` | ✅ | — | Default dashboard overview |
| `GET` | `/summary-by-date` | ✅ | `start`, `end` (ISO DateTime) | Filtered summary |
| `GET` | `/category-summary` | ✅ | `start`, `end` | Spending by category |
| `GET` | `/monthly` | ✅ | `year` | Monthly breakdown for charts |
| `GET` | `/recent` | ✅ | — | Recent 5–10 expenses |
| `GET` | `/compare` | ✅ | — | Current vs previous month |
| `GET` | `/top-category` | ✅ | — | Highest spending category |

---

### 📥 Reports — `/api/report/download`

> All endpoints accept `?format=excel` or `?format=pdf`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/summary` | ✅ | Download summary report |
| `GET` | `/category` | ✅ | Download category report |
| `GET` | `/full` | ✅ | Download full expense report |
| `GET` | `/yearly` | ✅ | Download yearly report |
| `GET` | `/budget` | ✅ | Download budget report |

---

### 💳 Payments — `/api/payment`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/create-order` | ✅ | Create Razorpay order (`?amount=`) |
| `POST` | `/verify` | ✅ | Verify payment signature |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/)
- **MySQL 8.0+** — running locally or via Docker
- **Redis** — running locally or via Docker
- **Docker** *(optional)* — for containerized deployment

---

### Environment Variables

Create a `.env` file or export the following variables before running:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/expense_tracker_db?createDatabaseIfNotExist=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword

# JWT
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION=86400000

# Redis
SPRING_REDIS_URL=redis://localhost:6379

# Mail (SMTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=youremail@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Twilio (SMS)
TWILIO_ACCOUNT_SID=your-twilio-sid
TWILIO_AUTH_TOKEN=your-twilio-token
TWILIO_PHONE_NUMBER=+1xxxxxxxxxx

# Razorpay
RAZORPAY_KEY_ID=your-razorpay-key
RAZORPAY_KEY_SECRET=your-razorpay-secret

# Server
PORT=8080
```

---

### Run Locally

```bash
# 1. Clone the repository
git clone https://github.com/your-username/expanse-tracker-backend.git
cd expanse-tracker-backend

# 2. Set environment variables (Linux/macOS)
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/expense_tracker_db?createDatabaseIfNotExist=true
export SPRING_DATASOURCE_USERNAME=root
# ... (set remaining vars)

# 3. Build and run
./mvnw spring-boot:run

# OR: Build the jar first, then run
./mvnw clean package -DskipTests
java -jar target/expanse-tracker-backend-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

---

### Run with Docker

The project includes a **multi-stage Dockerfile** that builds the jar inside the container — no local Java install needed.

```bash
# Build the Docker image
docker build -t expanse-tracker-backend .

# Run the container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/expense_tracker_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  -e JWT_SECRET=your-256-bit-secret \
  -e JWT_EXPIRATION=86400000 \
  -e SPRING_REDIS_URL=redis://host.docker.internal:6379 \
  expanse-tracker-backend
```

#### Using Docker Compose (recommended)

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/expense_tracker_db?createDatabaseIfNotExist=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
      JWT_SECRET: your-256-bit-secret
      JWT_EXPIRATION: 86400000
      SPRING_REDIS_URL: redis://redis:6379
    depends_on:
      - db
      - redis

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: expense_tracker_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  mysql_data:
```

```bash
docker-compose up --build
```

---

## ⚙️ Configuration Profiles

| Profile | File | Usage |
|---------|------|-------|
| Default (prod) | `application.properties` | All values via environment variables |
| `dev` | `application-dev.properties` | Local dev overrides |
| `test` | `application-test.properties` | H2 in-memory DB for unit tests |

Activate a profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 🧩 Design Patterns Used

| Pattern | Where | Purpose |
|---------|-------|---------|
| **Strategy** | `strategy/` (Reports) | Swap report formats (Summary, Full, Category, Monthly, Budget) without conditionals |
| **Strategy** | `notification/strategy/` | Plug in Email or SMS notification interchangeably |
| **Factory** | `notification/factory/` | Create the correct `NotificationStrategy` at runtime |
| **DTO Pattern** | `dtos/` | Decouple API contracts from internal entities |
| **Repository Pattern** | `repository/` | Abstract all database interactions |
| **Filter Chain** | `config/JwtFilter.java` | Intercept requests for JWT validation before hitting controllers |

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to your branch: `git push origin feature/your-feature-name`
5. Open a **Pull Request**

Please ensure:
- Code compiles with `./mvnw clean package`
- New endpoints are documented in the README
- Sensitive credentials are never committed — use environment variables

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Built with ❤️ using Spring Boot · Java 21 · MySQL · Redis
</p>
