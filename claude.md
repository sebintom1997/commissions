# Commission Management System

## Project Overview

A comprehensive Spring Boot application for managing salesperson commissions in a recruiting agency. The system tracks contractor and permanent placements, calculates commissions based on complex business rules, and manages the complete lifecycle from placement creation to commission payout.

### Business Domain

The system manages:
- **Salespeople** - Employees who place contractors/candidates
- **Clients** - Companies that hire contractors or candidates
- **Contractors** - Workers placed at client sites
- **Placements** - Contracts between clients and contractors, managed by salespeople
- **Commissions** - Calculated earnings for salespeople based on placement performance
- **Policy Settings** - Configurable business rules and percentages

### Key Features

- ✅ Complete CRUD operations for all entities
- ✅ Automatic commission calculations with tiered percentages
- ✅ Sequence-based commission rates (1st, 2nd, 3rd+ contracts)
- ✅ Financial calculations: margins, overheads, pay costs
- ✅ Support for both contractor and permanent placements
- ✅ RESTful API with pagination and filtering
- ✅ Database migrations with Flyway
- ✅ Comprehensive validation and error handling
- ✅ API documentation with Swagger/OpenAPI

---

## Technology Stack

### Core Technologies
- **Java 17** - Programming language
- **Spring Boot 3.5.7** - Application framework
- **PostgreSQL 17.5** - Relational database
- **Maven** - Build tool and dependency management

### Key Dependencies
- **Spring Data JPA** - Data persistence and ORM
- **Hibernate** - JPA implementation
- **Flyway** - Database version control and migrations
- **Lombok** - Boilerplate code reduction
- **HikariCP** - High-performance JDBC connection pool
- **SpringDoc OpenAPI** - API documentation (Swagger UI)
- **Jakarta Validation** - Request validation

### Development Tools
- **SLF4J + Logback** - Logging framework
- **JUnit 5** - Unit testing
- **AssertJ** - Fluent assertions for tests

---

## Architecture

### 6-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      REST API Layer                          │
│  Controllers - Handle HTTP requests/responses                │
│  @RestController, @RequestMapping, @Valid                   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      DTO Layer                               │
│  Request DTOs - Input validation                            │
│  Response DTOs - Output formatting                          │
│  Mappers - Entity ↔ DTO conversion                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│  Business logic and orchestration                           │
│  @Service, @Transactional                                   │
│  Interfaces + Implementations                               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  Repository Layer                            │
│  Data access using Spring Data JPA                          │
│  @Repository, JpaRepository                                 │
│  Custom queries with @Query                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Entity Layer                              │
│  Domain models mapped to database tables                    │
│  @Entity, @Table, relationships                             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                 PostgreSQL Database                          │
│  Managed by Flyway migrations                               │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns

1. **Repository Pattern** - Data access abstraction via Spring Data JPA
2. **Service Layer Pattern** - Business logic separation from controllers
3. **DTO Pattern** - Decoupling API contracts from domain models
4. **Singleton Pattern** - PolicySettings (one configuration record)
5. **Dependency Injection** - Constructor injection with `@RequiredArgsConstructor`
6. **Builder Pattern** - Used in response objects and test data

### Key Design Decisions

- **BigDecimal for Money** - Ensures precision in financial calculations (no float/double)
- **Lazy Loading** - `@ManyToOne(fetch = LAZY)` for performance optimization
- **Validation at Controller** - `@Valid` ensures data integrity before service layer
- **Immutable Calculated Fields** - Stored for audit and consistency
- **Flyway for Schema** - Version-controlled database changes
- **Separate Read/Write DTOs** - CreateRequest, UpdateRequest, Response for clarity

---

## Domain Model

### Entity Relationship Diagram

```
┌─────────────────┐
│   Salesperson   │
│─────────────────│
│ id              │
│ name            │──────┐
│ email*          │      │
│ phone           │      │
│ status          │      │
│ commission_rate │      │
└─────────────────┘      │
                         │ 1
                         │
                         │
                         │ *
                    ┌────▼────────┐
                    │  Placement  │◄───────────────┐
                    │─────────────│                │
                    │ id          │                │
                    │ type        │                │ *
                    │ status      │                │
                    │ start_date  │                │
                    │ end_date    │                │
                    │ bill_rate   │                │
                    │ commission% │                │
                    │ commission$ │                │
                    │ margins     │                │
                    │ sequence_#  │                │
                    └─────────────┘                │
                         │ *                       │
                         │                         │
                    ┌────┴──────┐                 │
                    │           │                 │
                 *  │        *  │                 │
        ┌───────────▼─┐    ┌───▼──────────┐      │
        │   Client    │    │  Contractor  │      │
        │─────────────│    │──────────────│      │
        │ id          │    │ id           │      │
        │ name*       │    │ name         │      │
        │ email*      │    │ email        │──────┘
        │ phone       │    │ phone        │  (counts for
        │ address     │    │ type         │   sequence#)
        │ status      │    │ status       │
        │ contact     │    └──────────────┘
        └─────────────┘

┌──────────────────┐
│ PolicySettings  │  (Singleton)
│─────────────────│
│ id              │
│ leave%          │
│ prsi%           │
│ pension%        │
│ admin%          │
│ insurance%      │
│ weeks_per_year  │
│ commission_1st  │
│ commission_2nd  │
│ commission_3rd+ │
└─────────────────┘
```

### Entities

#### 1. Salesperson
**Location:** `src/main/java/com/ContractBilling/commissions/entity/Salesperson.java`

Represents sales employees who create placements.

**Fields:**
- `id` (Long) - Primary key
- `name` (String) - Full name, required, max 100 chars
- `email` (String) - Unique, required, max 100 chars
- `phone` (String) - Optional, max 20 chars
- `status` (SalespersonStatus) - ACTIVE or INACTIVE
- `commissionRate` (BigDecimal) - Optional override rate
- `createdAt`, `updatedAt` (LocalDateTime) - Audit timestamps

**Relationships:**
- One-to-Many with Placement

#### 2. Client
**Location:** `src/main/java/com/ContractBilling/commissions/entity/Client.java`

Companies that hire contractors or candidates.

**Fields:**
- `id` (Long) - Primary key
- `name` (String) - Unique, required, max 100 chars
- `email` (String) - Unique, required
- `phone` (String) - Optional
- `address` (String) - Optional, max 500 chars
- `contactPerson` (String) - Optional, max 100 chars
- `status` (ClientStatus) - ACTIVE or INACTIVE
- `createdAt`, `updatedAt` (LocalDateTime)

**Relationships:**
- One-to-Many with Placement

#### 3. Contractor
**Location:** `src/main/java/com/ContractBilling/commissions/entity/Contractor.java`

Workers who are placed at client sites.

**Fields:**
- `id` (Long) - Primary key
- `name` (String) - Required, max 100 chars
- `email` (String) - Unique if provided, optional
- `phone` (String) - Optional
- `type` (ContractorType) - CONTRACTOR or PERMANENT
- `status` (ContractorStatus) - ACTIVE or INACTIVE
- `createdAt`, `updatedAt` (LocalDateTime)

**Relationships:**
- One-to-Many with Placement

#### 4. Placement (Core Entity)
**Location:** `src/main/java/com/ContractBilling/commissions/entity/Placement.java`

Represents a contract/placement between a contractor and client.

**Core Fields:**
- `id` (Long) - Primary key
- `placementType` (PlacementType) - CONTRACTOR or PERMANENT
- `status` (PlacementStatus) - DRAFT, ACTIVE, COMPLETED, TERMINATED
- `startDate`, `endDate` (LocalDate) - Contract period
- `sequenceNumber` (Integer) - 1st, 2nd, 3rd+ contract for this contractor at this client

**Contractor-Specific Fields:**
- `hoursPerWeek` (BigDecimal) - Working hours per week
- `weeksPerYear` (Integer) - Billable weeks (typically 45 or 52)
- `payType` (PayType) - HOURLY or SALARY
- `annualSalary` (BigDecimal) - If salaried
- `hourlyPayRate` (BigDecimal) - If hourly
- `billRate` (BigDecimal) - Rate charged to client per hour
- `adminPercentage` (BigDecimal) - Overhead percentage
- `insurancePercentage` (BigDecimal) - Overhead percentage
- `fixedCosts` (BigDecimal) - Fixed overhead costs

**Calculated Fields (Auto-computed):**
- `hourlyPayCost` (BigDecimal) - True cost per hour with load factors
- `marginPerHour` (BigDecimal) - Profit per hour
- `weeklyMargin` (BigDecimal) - Profit per week
- `grossAnnualMargin` (BigDecimal) - Annual profit before overheads
- `netAnnualMargin` (BigDecimal) - Annual profit after overheads
- `commissionPercentage` (BigDecimal) - Based on sequence number
- `commissionTotal` (BigDecimal) - Total commission amount

**Permanent-Specific Fields:**
- `placementFee` (BigDecimal) - Fee charged for placement
- `feeType` (FeeType) - PERCENTAGE or FLAT
- `candidateSalary` (BigDecimal) - Candidate's salary
- `recognitionPeriodMonths` (Integer) - Commission recognition period (default 12)

**Relationships:**
- Many-to-One with Salesperson
- Many-to-One with Client
- Many-to-One with Contractor

#### 5. PolicySettings (Singleton)
**Location:** `src/main/java/com/ContractBilling/commissions/entity/PolicySettings.java`

System-wide configuration for calculations. Only one record exists.

**Fields:**
- `id` (Long) - Primary key
- `leavePercentage` (BigDecimal) - Leave load factor (e.g., 14.54%)
- `prsiPercentage` (BigDecimal) - Social insurance (e.g., 11.25%)
- `pensionPercentage` (BigDecimal) - Pension contribution (e.g., 1.5%)
- `pensionCap` (BigDecimal) - Optional pension cap
- `adminPercentage` (BigDecimal) - Admin overhead (e.g., 6%)
- `insurancePercentage` (BigDecimal) - Insurance overhead (e.g., 2%)
- `weeksPerYear` (Integer) - Default billable weeks (e.g., 45)
- `firstContractCommission` (BigDecimal) - 1st contract rate (e.g., 15%)
- `secondContractCommission` (BigDecimal) - 2nd contract rate (e.g., 10%)
- `thirdContractCommission` (BigDecimal) - 3rd+ contract rate (e.g., 8%)
- `drawdownMinMonth` (Integer) - Minimum months before payout
- `drawdownMaxPerQuarter` (Integer) - Max payouts per quarter
- `updatedBy` (String) - Last person to update settings

---

## Services

### 1. SalespersonService
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/SalespersonServiceImpl.java`

**Operations:**
- Create, read, update, delete salespeople
- Get all with pagination
- Find by email or status
- Validate unique email on create/update

**Business Rules:**
- Email must be unique
- Name cannot be empty
- Default status: ACTIVE

### 2. ClientService
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/ClientServiceImpl.java`

**Operations:**
- Full CRUD with pagination
- Find by email or status
- Validate unique name and email

**Business Rules:**
- Name must be unique (case-insensitive)
- Email must be unique
- Cannot delete if client has placements (foreign key constraint)

### 3. ContractorService
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/ContractorServiceImpl.java`

**Operations:**
- Full CRUD with pagination
- Find by type (CONTRACTOR or PERMANENT)
- Conditional email validation

**Business Rules:**
- Email is optional but must be unique if provided
- Cannot delete if contractor has placements

### 4. SettingsService (Singleton Management)
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/SettingsServiceImpl.java`

**Operations:**
- Get current settings (always returns the single record)
- Update settings with updatedBy tracking
- Initialize default settings on startup via `@PostConstruct`

**Business Rules:**
- Only one settings record exists (enforced by service)
- Cannot create or delete settings
- All updates require updatedBy parameter

### 5. PlacementService
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/PlacementServiceImpl.java`

**Operations:**
- Create placement with automatic calculations
- Update placement with recalculation on financial field changes
- Full CRUD with pagination
- Find by salesperson, client, contractor, status, or type

**Business Rules:**
- Validates related entities exist (salesperson, client, contractor)
- Auto-calculates sequence number (count of previous placements)
- Sets default overhead percentages from PolicySettings
- Triggers commission calculation on create
- Recalculates commission when financial fields updated
- Cannot delete if referenced by other entities

**Calculation Trigger Logic:**
```java
On Create:
1. Fetch salesperson, client, contractor
2. Get PolicySettings
3. Calculate sequence number (contractor + client count + 1)
4. Set default admin% and insurance% from settings
5. Call CommissionCalculationService based on type
6. Save with all calculated fields

On Update:
1. Update fields from request
2. Check if financial fields changed
3. If yes, fetch settings and recalculate
4. Save updated entity
```

### 6. CommissionCalculationService
**Location:** `src/main/java/com/ContractBilling/commissions/service/impl/CommissionCalculationServiceImpl.java`

Core calculation engine for all financial computations.

**Methods:**

#### calculateHourlyPayCost(salary, hours, settings)
Calculates true hourly cost including all load factors.

**Formula:**
```
hourlyPayCost = (annualSalary ÷ 52 ÷ hoursPerWeek)
                × (1 + leave%)
                × (1 + PRSI%)
                × (1 + pension%)
```

**Example:**
- Salary: €55,000
- Hours/Week: 39
- Leave: 14.54%, PRSI: 11.25%, Pension: 1.5%
- **Result: €35.08/hour**

#### calculateMarginPerHour(billRate, payRate)
Calculates profit per hour.

**Formula:**
```
marginPerHour = billRate - hourlyPayCost
```

**Example:**
- Bill Rate: €40.28
- Pay Cost: €35.08
- **Result: €5.20/hour**

#### calculateWeeklyMargin(marginPerHour, hours)
**Formula:**
```
weeklyMargin = marginPerHour × hoursPerWeek
```

**Example:** €5.20 × 39 = **€202.80/week**

#### calculateGrossAnnualMargin(weeklyMargin, weeks)
**Formula:**
```
grossMargin = weeklyMargin × weeksPerYear
```

**Example:** €202.80 × 45 = **€9,126.00/year**

#### calculateNetAnnualMargin(gross, admin%, insurance%, fixed)
Calculates profit after overheads.

**Formula:**
```
adminCost = gross × (admin% ÷ 100)
insuranceCost = gross × (insurance% ÷ 100)
overheads = adminCost + insuranceCost + fixedCosts
netMargin = gross - overheads
```

**Example:**
- Gross: €9,126.00
- Admin: 6%, Insurance: 2%
- Overheads: €547.56 + €182.52 = €730.08
- **Net: €8,395.92/year**

#### determineCommissionPercentage(sequence, settings)
Returns commission percentage based on contract sequence.

**Rules:**
- Sequence 1 → 15% (firstContractCommission)
- Sequence 2 → 10% (secondContractCommission)
- Sequence 3+ → 8% (thirdContractCommission)

#### calculateCommissionTotal(netMargin, percentage)
**Formula:**
```
commission = netMargin × (percentage ÷ 100)
```

**Example:**
- Net Margin: €8,395.92
- Commission %: 15%
- **Total: €1,259.39**

#### calculateContractorCommission(placement, settings)
Orchestrates all calculations and sets all calculated fields on the placement entity.

#### calculatePermanentCommission(placement, settings)
Simplified calculation for permanent placements based on placement fee.

---

## REST API

### Base URL
```
http://localhost:8080/api
```

### API Documentation
Swagger UI available at: `http://localhost:8080/swagger-ui.html`

### Common Response Patterns

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2025-11-11T10:30:00"
}
```

**Error Response:**
```json
{
  "timestamp": "2025-11-11T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Salesperson not found with id: 123",
  "path": "/api/salespeople/123",
  "fieldErrors": {
    "email": "Email already exists"
  }
}
```

### Salesperson Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/salespeople` | Create salesperson |
| GET | `/api/salespeople` | List all (paginated) |
| GET | `/api/salespeople/{id}` | Get by ID |
| PUT | `/api/salespeople/{id}` | Update salesperson |
| DELETE | `/api/salespeople/{id}` | Delete salesperson |
| GET | `/api/salespeople/email/{email}` | Find by email |
| GET | `/api/salespeople/status/{status}` | Filter by status |

**Pagination Parameters:**
- `page` - Page number (0-indexed)
- `size` - Items per page (default: 20)
- `sort` - Sort field and direction (e.g., `createdAt,desc`)

### Client Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/clients` | Create client |
| GET | `/api/clients` | List all (paginated) |
| GET | `/api/clients/{id}` | Get by ID |
| PUT | `/api/clients/{id}` | Update client |
| DELETE | `/api/clients/{id}` | Delete client |
| GET | `/api/clients/email/{email}` | Find by email |
| GET | `/api/clients/status/{status}` | Filter by status |

### Contractor Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/contractors` | Create contractor |
| GET | `/api/contractors` | List all (paginated) |
| GET | `/api/contractors/{id}` | Get by ID |
| PUT | `/api/contractors/{id}` | Update contractor |
| DELETE | `/api/contractors/{id}` | Delete contractor |
| GET | `/api/contractors/email/{email}` | Find by email |
| GET | `/api/contractors/status/{status}` | Filter by status |
| GET | `/api/contractors/type/{type}` | Filter by type |

### Settings Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/settings` | Get current settings |
| PUT | `/api/settings` | Update settings |

**Note:** No POST or DELETE - settings are singleton.

### Placement Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/placements` | Create placement (auto-calculates commission) |
| GET | `/api/placements` | List all (paginated) |
| GET | `/api/placements/{id}` | Get by ID with full details |
| PUT | `/api/placements/{id}` | Update (recalculates if needed) |
| DELETE | `/api/placements/{id}` | Delete placement |
| GET | `/api/placements/salesperson/{id}` | Get all for salesperson |
| GET | `/api/placements/client/{id}` | Get all for client |
| GET | `/api/placements/contractor/{id}` | Get all for contractor |
| GET | `/api/placements/status/{status}` | Filter by status |
| GET | `/api/placements/type/{type}` | Filter by type |

### Example Request/Response

**Create Contractor Placement:**

```bash
POST /api/placements
Content-Type: application/json

{
  "salespersonId": 1,
  "clientId": 2,
  "contractorId": 3,
  "placementType": "CONTRACTOR",
  "startDate": "2025-01-15",
  "endDate": "2025-12-31",
  "hoursPerWeek": 39,
  "weeksPerYear": 45,
  "payType": "SALARY",
  "annualSalary": 55000,
  "billRate": 40.28
}
```

**Response:**

```json
{
  "success": true,
  "message": "Placement created successfully",
  "data": {
    "id": 10,
    "salesperson": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com"
    },
    "client": {
      "id": 2,
      "name": "Acme Corp",
      "email": "contact@acme.com"
    },
    "contractor": {
      "id": 3,
      "name": "Jane Smith",
      "email": "jane@example.com"
    },
    "placementType": "CONTRACTOR",
    "status": "DRAFT",
    "startDate": "2025-01-15",
    "endDate": "2025-12-31",
    "hoursPerWeek": 39.00,
    "weeksPerYear": 45,
    "annualSalary": 55000.00,
    "billRate": 40.28,
    "adminPercentage": 6.00,
    "insurancePercentage": 2.00,
    "hourlyPayCost": 35.08,
    "marginPerHour": 5.20,
    "weeklyMargin": 202.80,
    "grossAnnualMargin": 9126.00,
    "netAnnualMargin": 8395.92,
    "sequenceNumber": 1,
    "commissionPercentage": 15.00,
    "commissionTotal": 1259.39,
    "createdAt": "2025-11-11T10:30:00",
    "updatedAt": "2025-11-11T10:30:00"
  },
  "timestamp": "2025-11-11T10:30:00"
}
```

---

## Database Schema

### Tables Overview

1. **salesperson** - Sales employees
2. **client** - Client companies
3. **contractor** - Workers/candidates
4. **placement** - Placement contracts (core table)
5. **policy_settings** - System configuration (singleton)
6. **flyway_schema_history** - Migration tracking (auto-managed)

### Migrations

**Location:** `src/main/resources/db/migration/`

| File | Description |
|------|-------------|
| V1__init_schema.sql | Salesperson table + indexes |
| V2__create_client_table.sql | Client table + indexes |
| V3__create_contractor_table.sql | Contractor table + indexes |
| V4__create_policy_settings_table.sql | Settings table + default data |
| V5__add_performance_indexes.sql | Performance optimization indexes |
| V6__create_placement_table.sql | Placement table + indexes |

### Key Indexes

**Performance Indexes:**
- `idx_salesperson_email` - Unique constraint on email
- `idx_salesperson_status_created` - Filter by status + sort by date
- `idx_client_name_lower` - Case-insensitive name search
- `idx_contractor_type_status` - Filter by type and status
- `idx_placement_contractor_client` - Count for sequence calculation
- `idx_placement_status_created_at` - Common filter/sort combination

**Foreign Key Indexes:**
- All foreign keys have indexes for join performance

### Data Types

**Financial Fields:**
- `DECIMAL(12, 2)` - Money amounts (e.g., salaries, fees)
- `DECIMAL(10, 2)` - Hourly rates, margins
- `DECIMAL(5, 2)` - Percentages

**Text Fields:**
- `VARCHAR(100)` - Names, emails
- `VARCHAR(500)` - Addresses, notes

**Dates:**
- `DATE` - Start/end dates
- `TIMESTAMP` - Audit timestamps (with DEFAULT CURRENT_TIMESTAMP)

---

## Configuration

### Application Properties

**Location:** `src/main/resources/application.properties`

```properties
# Application
spring.application.name=commissions

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/commissions
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000

# Logging
logging.level.com.ContractBilling.commissions=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# API Documentation
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Key Settings

- **ddl-auto=validate** - Flyway manages schema, Hibernate only validates
- **HikariCP** - 10 max connections, 5 minimum idle
- **Flyway baseline** - Allows migration on existing database
- **Logging** - INFO for application, DEBUG for SQL queries

---

## Commission Calculation Examples

### Example 1: First Contract (15% Commission)

**Input:**
- Annual Salary: €55,000
- Hours/Week: 39
- Weeks/Year: 45
- Bill Rate: €40.28/hour
- Sequence: 1 (first contract at this client)

**Calculation Steps:**

1. **Hourly Pay Cost:**
   ```
   Base Rate = 55,000 ÷ 52 ÷ 39 = €27.13
   With Load Factors = 27.13 × 1.1454 × 1.1125 × 1.015 = €35.08
   ```

2. **Margin Per Hour:**
   ```
   40.28 - 35.08 = €5.20
   ```

3. **Weekly Margin:**
   ```
   5.20 × 39 = €202.80
   ```

4. **Gross Annual Margin:**
   ```
   202.80 × 45 = €9,126.00
   ```

5. **Net Annual Margin:**
   ```
   Admin Cost = 9,126 × 0.06 = €547.56
   Insurance Cost = 9,126 × 0.02 = €182.52
   Overheads = 547.56 + 182.52 = €730.08
   Net = 9,126.00 - 730.08 = €8,395.92
   ```

6. **Commission:**
   ```
   8,395.92 × 0.15 = €1,259.39
   ```

### Example 2: Second Contract (10% Commission)

Same contractor placed at same client again:
- Sequence: 2
- All other values same as Example 1
- Net Annual Margin: €8,395.92
- **Commission: €8,395.92 × 0.10 = €839.59**

### Example 3: Third Contract (8% Commission)

Third placement of same contractor at same client:
- Sequence: 3
- Net Annual Margin: €8,395.92
- **Commission: €8,395.92 × 0.08 = €671.67**

### Example 4: With Fixed Costs

Same as Example 1, but with €500 fixed costs:
- Gross: €9,126.00
- Overheads: €730.08 + €500 = €1,230.08
- Net: €9,126.00 - €1,230.08 = €7,895.92
- **Commission: €7,895.92 × 0.15 = €1,184.39**

### Example 5: 52-Week Year

Same as Example 1, but 52 weeks instead of 45:
- Weekly Margin: €202.80
- Gross: €202.80 × 52 = €10,545.60
- Overheads: €843.65
- Net: €9,701.95
- **Commission: €9,701.95 × 0.15 = €1,455.29**

### Example 6: Permanent Placement

**Input:**
- Placement Fee: €10,000
- Sequence: 1

**Calculation:**
- Net Margin = Placement Fee = €10,000
- Commission % = 15% (first contract)
- **Commission Total: €10,000 × 0.15 = €1,500.00**

---

## Running the Application

### Prerequisites

1. **Java 17** or higher
2. **PostgreSQL 17.5** running on localhost:5432
3. **Maven 3.6+** (or use included `./mvnw`)

### Database Setup

```bash
# Create database
psql -U postgres
CREATE DATABASE commissions;
\q
```

### Build and Run

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Run application
./mvnw spring-boot:run
```

Application starts on: `http://localhost:8080`

### First-Time Setup

On first run, Flyway will:
1. Create all tables
2. Insert default PolicySettings
3. Create all indexes

### Accessing the API

**Swagger UI:**
```
http://localhost:8080/swagger-ui.html
```

**Health Check:**
```
http://localhost:8080/actuator/health
```

**API Docs (JSON):**
```
http://localhost:8080/v3/api-docs
```

---

## Testing

### Test Structure

```
src/test/java/com/ContractBilling/commissions/
├── service/
│   └── CommissionCalculationServiceTest.java    (16 tests)
└── CommissionsApplicationTests.java             (1 test)
```

### Running Tests

```bash
# All tests
./mvnw test

# Specific test class
./mvnw test -Dtest=CommissionCalculationServiceTest

# With coverage
./mvnw clean test jacoco:report
```

### Test Coverage

**CommissionCalculationServiceTest** - Comprehensive unit tests:
- ✓ Individual calculation methods
- ✓ Full end-to-end scenarios
- ✓ Different sequence numbers (1st, 2nd, 3rd)
- ✓ Edge cases (fixed costs, 52 weeks)
- ✓ Permanent placements
- ✓ Precision and rounding validation

All 16 tests pass with expected values validated.

---

## Error Handling

### Exception Types

1. **ResourceNotFoundException** (404)
   - Entity not found by ID
   - Example: "Salesperson not found with id: 123"

2. **DuplicateResourceException** (409)
   - Unique constraint violation
   - Example: "Email already exists: john@example.com"

3. **ValidationException** (400)
   - Invalid request data
   - Returns field-level errors

4. **Internal Server Error** (500)
   - Unexpected errors
   - Logged for debugging

### Global Exception Handler

**Location:** `src/main/java/com/ContractBilling/commissions/exception/GlobalExceptionHandler.java`

Provides consistent error responses across all endpoints with:
- Timestamp
- HTTP status code
- Error message
- Request path
- Field-level validation errors (if applicable)

---

## Data Validation

### Validation Rules

**Salesperson:**
- `name` - @NotBlank, max 100 chars
- `email` - @NotBlank, @Email, unique, max 100 chars
- `phone` - max 20 chars
- `commissionRate` - min 0, max 100

**Client:**
- `name` - @NotBlank, unique (case-insensitive), max 100 chars
- `email` - @NotBlank, @Email, unique
- `contactPerson` - max 100 chars
- `address` - max 500 chars

**Contractor:**
- `name` - @NotBlank, max 100 chars
- `email` - @Email, unique if provided
- `type` - @NotNull (CONTRACTOR or PERMANENT)

**Placement:**
- `salespersonId` - @NotNull
- `clientId` - @NotNull
- `contractorId` - @NotNull
- `placementType` - @NotNull
- `annualSalary` - min 0 (if provided)
- `billRate` - min 0, max 10000
- `hoursPerWeek` - min 0, max 168

**PolicySettings:**
- All percentages - min 0, max 100
- All amounts - min 0
- `weeksPerYear` - min 1, max 52

---

## Security Considerations

### Current Implementation

- ✅ Input validation on all endpoints
- ✅ SQL injection prevention via JPA/Hibernate
- ✅ Unique constraints on emails
- ✅ Foreign key constraints for data integrity
- ✅ Transaction management with rollback
- ✅ Audit timestamps on all entities

### Future Enhancements (Not Implemented)

- Authentication & Authorization (Spring Security)
- API rate limiting
- JWT tokens
- Role-based access control (Admin, Sales, Manager)
- Audit logging for sensitive operations
- Data encryption at rest

---

## Performance Optimizations

### Implemented

1. **Connection Pooling** - HikariCP with 10 max connections
2. **Lazy Loading** - @ManyToOne relationships with LAZY fetch
3. **Database Indexes** - 15+ indexes for common queries
4. **Pagination** - All list endpoints support pagination
5. **Query Optimization** - Custom queries where needed
6. **Composite Indexes** - For common filter combinations

### Query Performance

**Fast Queries (< 10ms):**
- Single entity by ID (primary key lookup)
- Find by email (unique index)
- Count queries for sequence

**Moderate Queries (10-50ms):**
- Paginated lists with sorting
- Filter by status + date
- Join queries for placements

**Considerations:**
- N+1 query prevention via proper fetch strategies
- Indexed foreign keys for joins
- Case-insensitive searches use function-based indexes

---

## Future Development Roadmap

### Phase 2: Accounting Engine (Days 9-18)
- Commission plan entity (tracks expected vs actual)
- Ledger entries for financial transactions
- Revenue recognition over time
- Drawdown (payout) management with rules
- Commission liability tracking

### Phase 3: Reporting (Days 19-25)
- Monthly commission reports
- Salesperson performance dashboards
- Client/contractor analytics
- Export to CSV/Excel
- Financial summaries

### Phase 4: Advanced Features (Days 26-30)
- User authentication & authorization
- Role-based access control
- Audit trail for all changes
- Email notifications
- API versioning
- Comprehensive integration tests
- Production deployment guide

---

## Troubleshooting

### Common Issues

**1. Database Connection Failed**
```
Error: Connection refused
Solution: Ensure PostgreSQL is running on localhost:5432
Check: pg_isready -h localhost -p 5432
```

**2. Flyway Migration Failed**
```
Error: Script V1__init_schema.sql failed - relation already exists
Solution: Drop database and recreate, or run Flyway repair
Command: ./mvnw flyway:repair
```

**3. Port 8080 Already in Use**
```
Error: Port 8080 is already in use
Solution: Change port in application.properties
Add: server.port=8081
```

**4. Test Failure - Context Load**
```
Error: Failed to load ApplicationContext
Solution: Check database is running and accessible
Ensure: spring.datasource.url is correct
```

### Logs Location

```
Console: Standard output
Database: Check PostgreSQL logs
Location: /var/log/postgresql/ (Linux) or pg_log directory
```

---

## Code Locations Reference

### Core Business Logic
- **Entities:** `src/main/java/com/ContractBilling/commissions/entity/`
- **Services:** `src/main/java/com/ContractBilling/commissions/service/impl/`
- **Calculations:** `CommissionCalculationServiceImpl.java`

### API Layer
- **Controllers:** `src/main/java/com/ContractBilling/commissions/controller/`
- **DTOs:** `src/main/java/com/ContractBilling/commissions/dto/`
- **Mappers:** `src/main/java/com/ContractBilling/commissions/dto/*Mapper.java`

### Data Access
- **Repositories:** `src/main/java/com/ContractBilling/commissions/repository/`
- **Migrations:** `src/main/resources/db/migration/`

### Configuration
- **Properties:** `src/main/resources/application.properties`
- **OpenAPI:** `src/main/java/com/ContractBilling/commissions/config/OpenApiConfig.java`

### Error Handling
- **Exceptions:** `src/main/java/com/ContractBilling/commissions/exception/`
- **Global Handler:** `GlobalExceptionHandler.java`

### Tests
- **Unit Tests:** `src/test/java/com/ContractBilling/commissions/service/`

---

## Development Guidelines

### Code Style

- **Constructor Injection** - Use `@RequiredArgsConstructor` with final fields
- **Lombok** - Use `@Data`, `@Builder`, `@Slf4j` to reduce boilerplate
- **Logging** - Log at INFO for important events, DEBUG for details
- **BigDecimal** - Always use for money/percentages, never float/double
- **Validation** - Validate at controller layer with `@Valid`
- **Transactions** - Use `@Transactional` on service methods

### Naming Conventions

- **Entities** - Singular noun (Salesperson, Client)
- **Tables** - Snake_case, singular (salesperson, client)
- **DTOs** - Purpose suffix (CreateRequest, Response)
- **Services** - Interface + Impl pattern
- **Repositories** - EntityNameRepository
- **Controllers** - EntityNameController

### Adding New Features

1. Create entity with annotations
2. Create DTOs (Create, Update, Response)
3. Create mapper component
4. Create repository interface
5. Create service interface + implementation
6. Create controller with REST endpoints
7. Create Flyway migration
8. Write unit tests
9. Update this documentation

---

## License

Internal project - All rights reserved

## Contact

For questions or support, contact the development team.

---

**Last Updated:** 2025-11-11
**Version:** 1.0 (Days 1-8 Complete)
**Status:** Development - Core features implemented, accounting engine pending
