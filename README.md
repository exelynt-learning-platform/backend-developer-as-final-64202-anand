
# Resource Booking System

A secure, RESTful Resource Booking System built with Spring Boot 2.7.18, Java 16/17+, Spring Security, JWT, and Hibernate/JPA (with support for MySQL, PostgreSQL, and H2 database).

The system allows users to search available resources and manage their own reservations. Administrators have full CRUD control over all resources and reservations.

---

## 🚀 Features

- JWT-Based Authentication: Login at `POST /auth/login` to obtain a stateless JWT token.
- Role-Based Access Control (RBAC): Secure authorization for `ADMIN` and `USER` roles using Spring Security's `@PreAuthorize` method security.
- Resource Management: Complete CRUD for resources (restricted to `ADMIN` for write operations; `USER` has read-only access).
- Reservation Lifecycle: Manage bookings with statuses: `PENDING`, `CONFIRMED`, and `CANCELLED`.
- Reservation Ownership: Users can only see, modify, or delete their own reservations. Administrators have access to view and manage all reservations across the system.
- Dynamic Filtering: Filter reservations dynamically by `status`, `minPrice`, and `maxPrice`.
- Pagination & Sorting: Paginate reservation results using standard `page` and `size` parameters with optional `sort` sorting.
- Data Initialization: Automatically seeds two testing users (`admin` and `user`) along with default resources on startup.
- Global Error Handling: Centralized exception handler providing clean, standardized JSON error maps for validation, security, and bad requests.

---

## 🛠️ Tech Stack

- Java: Version 16 or 17+ (Class file compatibility configured for Java 16+)
- Framework: Spring Boot 2.7.18
- Security: Spring Security & JWT (using `io.jsonwebtoken`)
- Database: H2 (in-memory, for quick testing), MySQL, or PostgreSQL
- Build Tool: Maven

---

## 📂 Project Structure

```
com.example.booking
 ├── BookingApplication.java         # Main bootstrap class
 ├── config/                         # Configuration files
 │    ├── DataInitializer.java       # Seeds initial users & resources
 │    ├── JwtAuthenticationFilter.java # Intercepts and parses JWT tokens
 │    ├── JwtProvider.java           # Generates and validates tokens
 │    └── SecurityConfig.java        # Security filter chains and RBAC rules
 ├── controller/                     # REST API Controllers
 │    ├── AuthController.java        # Handles POST /auth/login
 │    ├── ResourceController.java    # Resource CRUD
 │    └── ReservationController.java # Reservation CRUD
 ├── dto/                            # Data Transfer Objects
 │    ├── AuthRequest.java
 │    ├── AuthResponse.java
 │    ├── ResourceDto.java
 │    ├── ReservationDto.java
 │    └── ReservationFilterDto.java
 ├── entity/                         # JPA Entities
 │    ├── Role.java
 │    ├── User.java
 │    ├── Resource.java
 │    ├── ReservationStatus.java
 │    └── Reservation.java
 ├── exception/                      # Exception handling
 │    └── GlobalExceptionHandler.java # Map exceptions to HTTP status codes
 └── repository/                     # Spring Data JPA repositories
      ├── UserRepository.java
      ├── ResourceRepository.java
      └── ReservationRepository.java
```

---

## ⚙️ Configuration & Environment Variables

The database is configured in `src/main/resources/application.yml`. By default, it uses an in-memory H2 database for instant, zero-setup testing.

To connect to MySQL or PostgreSQL, configure the following environment variables:

| Environment Variable | Default Value | Description |
|----------------------|---------------|-------------|
| `DB_URL`             | `jdbc:h2:mem:bookingdb` (H2) | JDBC database connection URL |
| `DB_DRIVER`          | `org.h2.Driver` | JDBC driver class name |
| `DB_USERNAME`        | `sa` | Database username |
| `DB_PASSWORD`        | (empty) | Database password |
| `JWT_SECRET`         | `MySecretKeyForJWTthatIsAtLeast32BytesLong123` | Token signature secret |
| `JWT_EXPIRATION_MS`  | `3600000` (1 hour) | JWT token lifespan |

---

## 💿 Seed Data & Test Credentials

On application startup, the database is automatically seeded with the following credentials:

| Username | Password | Role |
|----------|----------|------|
| `admin`  | `admin`  | `ADMIN` |
| `user`   | `user`   | `USER` |

Passwords are securely hashed using BCrypt at runtime.

---

## 🏁 Quick Start & Run Commands

### 1. Run via Maven Wrapper (No Maven Install Required)
Make sure you run these commands in the project root directory (`C:\Users\HP\OneDrive\Desktop\Resource Booking System`):

Set your Java 16/17 JDK path and boot the application:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-16"
.\mvnw.cmd clean spring-boot:run
```

The application will launch on port `8081`.

---

## 📖 API Documentation & Postman

### Postman Collection
We have included a pre-configured Postman Collection file in the root of the project:  
`Resource_Booking_System.postman_collection.json`

How to use:
1. Import this file into Postman.
2. Send `1. Login - Admin` or `2. Login - User` to get your token.
3. Paste the token into the collection's `jwt_token` variable under the Variables tab.
4. Run any request (Authorization headers are automatically pre-configured!).

---

## 🔗 Endpoint List

### 1. Auth Endpoints
- `POST /auth/login` - Public login, returns JWT token.

### 2. Resource Endpoints (`/api/v1/resources`)
- `GET /api/v1/resources` - List all resources (USER/ADMIN).
- `GET /api/v1/resources/{id}` - Get resource by ID (USER/ADMIN).
- `POST /api/v1/resources` - Create a resource (ADMIN only).
- `PUT /api/v1/resources/{id}` - Update a resource (ADMIN only).
- `DELETE /api/v1/resources/{id}` - Delete a resource (ADMIN only).

### 3. Reservation Endpoints (`/api/v1/reservations`)
- `GET /api/v1/reservations` - List reservations (filtered, paginated, and sorted).
  - *Admin sees all; User sees only their own.*
- `POST /api/v1/reservations` - Book a resource (USER/ADMIN).
- `PUT /api/v1/reservations/{id}` - Update reservation details (USER can only edit own; ADMIN can edit any).
- `DELETE /api/v1/reservations/{id}` - Delete/cancel a reservation (USER can only delete own; ADMIN can delete any).

#### GET Query Parameters:
- `status`: `PENDING`, `CONFIRMED`, `CANCELLED`
- `minPrice` / `maxPrice`: decimal limits
- `page`: 0-indexed page number
- `size`: number of records per page
- `sort`: field name & direction (e.g. `price,desc` or `startTime,asc`)
