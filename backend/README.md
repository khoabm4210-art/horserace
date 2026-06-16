# Horse Racing Backend

Spring Boot 3.x REST API for Horse Racing Management System

## Features

- User authentication with JWT
- Horse and Jockey management
- Race registration and management
- Real-time notifications via WebSocket
- File upload support
- Audit logging
- Role-based access control

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

## Setup

### 1. Create MySQL Database

```sql
CREATE DATABASE horse_racing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configure Database Connection

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/horse_racing_db
    username: root
    password: your_password
```

### 3. Build & Run

```bash
mvn clean package
mvn spring-boot:run
```

Backend runs on: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`
API Docs: `http://localhost:8080/api-docs`

## Project Structure

```
src/main/java/com/horseracing/
├── config/          # Spring configurations
├── controller/      # REST controllers
├── service/         # Business logic
├── entity/          # JPA entities
├── dto/             # Data Transfer Objects
├── repository/      # JPA repositories
├── security/        # JWT & Security
├── storage/         # File storage abstraction
├── websocket/       # WebSocket handlers
├── enums/           # Enumerations
├── exception/       # Custom exceptions
└── aop/             # Aspect-oriented programming
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user
- `POST /api/v1/auth/refresh-token` - Refresh access token
- `GET /api/v1/auth/me` - Get current user info

### Horses
- `GET /api/v1/horses` - List all horses
- `POST /api/v1/horses` - Create new horse
- `GET /api/v1/horses/{id}` - Get horse details
- `PUT /api/v1/horses/{id}` - Update horse
- `DELETE /api/v1/horses/{id}` - Delete horse

### See full API documentation at `/swagger-ui.html`

## Testing

```bash
mvn test
```

## Documentation

See `PROMPT_BACKEND_SpringBoot_V3.md` for detailed specifications.
