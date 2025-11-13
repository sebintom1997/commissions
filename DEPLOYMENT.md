# Commission Management System - Deployment Guide

## Overview
This is a comprehensive Spring Boot application for managing commissions, placements, revenue recognition, and drawdown requests with JWT authentication.

## Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.8+
- Git

## Database Setup

### 1. Create Database
```sql
CREATE DATABASE commissions_db;
CREATE USER commissions_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE commissions_db TO commissions_user;
```

### 2. Create Test Database
```sql
CREATE DATABASE commissions_test_db;
GRANT ALL PRIVILEGES ON DATABASE commissions_test_db TO commissions_user;
```

## Application Configuration

### Development Configuration (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/commissions_db
spring.datasource.username=commissions_user
spring.datasource.password=password
jwt.secret=your-secret-key-change-in-production
jwt.expiration=3600000
```

### Production Configuration
Update `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://prod-db-host:5432/commissions_db
spring.datasource.username=prod_user
spring.datasource.password=secure_password
spring.jpa.hibernate.ddl-auto=validate
jwt.secret=your-very-long-secure-secret-key-min-32-chars
jwt.expiration=1800000
logging.level.root=WARN
```

## Build and Run

### Build
```bash
./mvnw clean package -DskipTests
```

### Run Locally
```bash
./mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw test
```

### Run Specific Profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Authentication

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

Response includes JWT token. Use in subsequent requests:
```bash
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/placements
```

## Database Migrations
Flyway automatically manages migrations from `src/main/resources/db/migration/`.
Current migrations:
- V1: Initial schema (Salesperson, Client)
- V2: Client table
- V3: Contractor table
- V4: Policy Settings
- V5: Performance indexes
- V6: Placement table
- V7: Commission Plan
- V8: Ledger tracking
- V9: Revenue Recognition Schedule
- V10: Drawdown Requests
- V11: User Authentication

## Key Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Placements
- `GET /api/placements` - List all placements
- `POST /api/placements` - Create placement (auto-creates commission plan & recognition)
- `GET /api/placements/{id}` - Get placement details
- `PUT /api/placements/{id}` - Update placement
- `DELETE /api/placements/{id}` - Delete placement

### Commission Plans
- `GET /api/commission-plans` - List plans
- `POST /api/commission-plans` - Create plan
- `GET /api/commission-plans/{id}` - Get plan details

### Revenue Recognition
- `GET /api/recognition-schedules` - List schedules
- `POST /api/recognition-schedules/{id}/recognize` - Recognize revenue

### Drawdown Requests
- `POST /api/drawdowns` - Request drawdown
- `GET /api/drawdowns/salesperson/{id}` - Get drawdown requests
- `POST /api/drawdowns/{id}/approve` - Approve drawdown
- `POST /api/drawdowns/{id}/pay` - Process payment

### Reports
- `GET /api/reports/salesperson/{id}/dashboard` - Salesperson dashboard
- `GET /api/reports/salesperson/{id}/period` - Period summary
- `GET /api/reports/top-performers` - Top performers list
- `GET /api/reports/health` - System health

## Deployment Checklist

- [ ] Update JWT secret in production config
- [ ] Configure PostgreSQL database with proper backups
- [ ] Set up logging and monitoring
- [ ] Configure CORS for frontend domain
- [ ] Enable HTTPS/SSL
- [ ] Set up rate limiting
- [ ] Configure database connection pooling
- [ ] Test all endpoints with valid JWT tokens
- [ ] Run load tests
- [ ] Set up health check endpoints
- [ ] Configure application monitoring and alerts
- [ ] Document API for team
- [ ] Set up CI/CD pipeline

## Docker Deployment

### Build Docker Image
```bash
docker build -t commissions-app:latest .
```

### Run Docker Container
```bash
docker run -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/commissions_db \
           -e SPRING_DATASOURCE_USERNAME=commissions_user \
           -e SPRING_DATASOURCE_PASSWORD=password \
           -e JWT_SECRET=your-secret \
           -p 8080:8080 commissions-app:latest
```

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check credentials in application.properties
- Ensure database exists and user has permissions

### JWT Token Issues
- Verify Authorization header format: `Bearer <token>`
- Check token expiration time
- Ensure JWT secret matches between encode/decode

### Migration Failures
- Check Flyway migration naming convention (V#__description.sql)
- Verify migration file encoding is UTF-8
- Check for syntax errors in SQL

## Support
Contact the development team for issues or feature requests.
