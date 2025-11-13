# 🚨 CRITICAL: Commission Management System Development Guidelines

## ⚠️ READ THIS BEFORE MAKING ANY CHANGES

This document contains **mandatory** guidelines for developing and maintaining the Commission Management System. All developers (human or AI) **MUST** follow these principles to maintain data integrity, calculation accuracy, and system reliability.

---

## 📋 Table of Contents

1. [Core Principles](#core-principles)
2. [Financial Calculations - CRITICAL](#financial-calculations---critical)
3. [Database Changes](#database-changes)
4. [Entity Modifications](#entity-modifications)
5. [Service Layer Changes](#service-layer-changes)
6. [API Endpoint Changes](#api-endpoint-changes)
7. [Testing Requirements](#testing-requirements)
8. [Common Mistakes to Avoid](#common-mistakes-to-avoid)
9. [Files to Review Checklist](#files-to-review-checklist)
10. [Commission Calculation Rules](#commission-calculation-rules)

---

## 🎯 Core Principles

### **NEVER Compromise On:**

1. **Financial Accuracy** - All money calculations MUST use `BigDecimal`, never `float` or `double`
2. **Data Integrity** - All entity relationships MUST be validated before saving
3. **Audit Trail** - All entities MUST have `createdAt` and `updatedAt` timestamps
4. **Transaction Safety** - All service methods that modify data MUST use `@Transactional`
5. **Calculation Immutability** - Once calculated, commission values MUST NOT be manually overridden

### **Always Follow:**

- Use BigDecimal with `HALF_UP` rounding for all financial operations
- Validate all inputs at the controller layer with `@Valid`
- Never delete entities that have dependencies (use soft delete or status change)
- Always log financial calculations at INFO level
- Test all calculations with known expected values

---

## 💰 CRITICAL: Financial Calculations

### ⛔ **DON'T Do This:**

```java
// ❌ WRONG - Using float/double for money
private float commission;
private double salary;

// ❌ WRONG - Direct arithmetic operations
BigDecimal result = amount1 + amount2;

// ❌ WRONG - No scale or rounding mode specified
BigDecimal result = amount.divide(divisor);

// ❌ WRONG - Manually setting calculated fields
placement.setCommissionTotal(new BigDecimal("1000"));

// ❌ WRONG - Hardcoded calculation values
BigDecimal rate = new BigDecimal("0.15");
```

### ✅ **DO This:**

```java
// ✅ CORRECT - Using BigDecimal
private BigDecimal commission;
private BigDecimal salary;

// ✅ CORRECT - Using BigDecimal methods
BigDecimal result = amount1.add(amount2);

// ✅ CORRECT - Always specify scale and rounding
BigDecimal result = amount.divide(divisor, 2, RoundingMode.HALF_UP);

// ✅ CORRECT - Use calculation service
calculationService.calculateContractorCommission(placement, settings);

// ✅ CORRECT - Get values from PolicySettings
BigDecimal rate = settings.getFirstContractCommission()
    .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
```

### **Mandatory Calculation Rules:**

1. **Sequence Number MUST be calculated** - Never manually set
   ```java
   // ALWAYS calculate from database
   int sequenceNumber = repository.countByContractorAndClient(contractor, client) + 1;
   ```

2. **All calculated fields MUST go through CommissionCalculationService**
   - hourlyPayCost
   - marginPerHour
   - weeklyMargin
   - grossAnnualMargin
   - netAnnualMargin
   - commissionPercentage
   - commissionTotal

3. **Recalculation Triggers** - Recalculate commission when ANY of these change:
   - annualSalary
   - hourlyPayRate
   - billRate
   - hoursPerWeek
   - weeksPerYear
   - placementFee

4. **PolicySettings MUST be used** - Never hardcode:
   - Leave percentage
   - PRSI percentage
   - Pension percentage
   - Admin percentage
   - Insurance percentage
   - Commission tier percentages

---

## 🗄️ Database Changes

### **CRITICAL Rules:**

1. **NEVER modify existing migrations** - Create a new migration file
2. **NEVER use JPA ddl-auto=update** - Always use Flyway
3. **ALWAYS name migrations correctly**: `V{number}__{description}.sql`
4. **ALWAYS test migrations on a copy of production data**

### ⛔ **DON'T Do This:**

```sql
-- ❌ WRONG - Deleting data without backup
DELETE FROM placement WHERE status = 'DRAFT';

-- ❌ WRONG - No constraint names
ALTER TABLE placement ADD FOREIGN KEY (client_id) REFERENCES client(id);

-- ❌ WRONG - Missing indexes on foreign keys
ALTER TABLE placement ADD COLUMN salesperson_id BIGINT;

-- ❌ WRONG - Using VARCHAR for money
ALTER TABLE placement ADD COLUMN commission FLOAT;
```

### ✅ **DO This:**

```sql
-- ✅ CORRECT - Update status instead of delete
UPDATE placement SET status = 'CANCELLED' WHERE status = 'DRAFT';

-- ✅ CORRECT - Named constraints
ALTER TABLE placement
  ADD CONSTRAINT fk_placement_client
  FOREIGN KEY (client_id) REFERENCES client(id)
  ON DELETE RESTRICT;

-- ✅ CORRECT - Create index for foreign key
CREATE INDEX idx_placement_salesperson_id ON placement(salesperson_id);

-- ✅ CORRECT - Use DECIMAL for money
ALTER TABLE placement ADD COLUMN commission DECIMAL(12, 2);
```

### **Financial Column Standards:**

```sql
-- Money amounts (salaries, fees, totals)
DECIMAL(12, 2)  -- Up to 9,999,999,999.99

-- Hourly rates, margins
DECIMAL(10, 2)  -- Up to 99,999,999.99

-- Percentages
DECIMAL(5, 2)   -- Up to 999.99 (allows percentages like 15.50%)
```

### **Migration Checklist:**

Before creating a migration:
- [ ] Check if table/column already exists
- [ ] Add rollback script in comments
- [ ] Include appropriate indexes
- [ ] Test with sample data
- [ ] Verify foreign key constraints
- [ ] Document breaking changes

---

## 📦 Entity Modifications

### **Before Changing Any Entity:**

1. **Check all relationships** - Will this break existing references?
2. **Review calculated fields** - Will this affect commission calculations?
3. **Check migration** - Does database schema need updating?
4. **Update DTOs** - Do Request/Response DTOs need changes?
5. **Update Mapper** - Does entity ↔ DTO mapping need updates?

### ⛔ **DON'T Do This:**

```java
// ❌ WRONG - Removing @NotNull without migration
// private String email;  // Was @NotNull, removed annotation

// ❌ WRONG - Changing relationship without cascade planning
@OneToMany(mappedBy = "placement", cascade = CascadeType.ALL)

// ❌ WRONG - Using EAGER fetch
@ManyToOne(fetch = FetchType.EAGER)

// ❌ WRONG - Mutable calculated fields
public void setCommissionTotal(BigDecimal total) {
    this.commissionTotal = total;
}
```

### ✅ **DO This:**

```java
// ✅ CORRECT - Keep validation constraints
@NotNull
@Email
private String email;

// ✅ CORRECT - Explicit cascade strategy
@OneToMany(mappedBy = "placement", cascade = {CascadeType.PERSIST})

// ✅ CORRECT - Use LAZY fetch
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "salesperson_id", nullable = false)

// ✅ CORRECT - Calculated fields should be package-private or not have setters
void setCommissionTotal(BigDecimal total) {
    this.commissionTotal = total;
}
```

### **Entity Change Checklist:**

When modifying an entity:
- [ ] Create Flyway migration for schema change
- [ ] Update all DTOs (Create, Update, Response)
- [ ] Update Mapper class
- [ ] Update Repository if adding query methods
- [ ] Update Service interface and implementation
- [ ] Update Controller if exposing new fields
- [ ] Update tests
- [ ] Update API documentation comments
- [ ] Verify all existing tests still pass

---

## 🔧 Service Layer Changes

### **Critical Service Rules:**

1. **ALWAYS use @Transactional** on methods that modify data
2. **ALWAYS validate related entities exist** before creating relationships
3. **ALWAYS log important operations** at INFO level
4. **NEVER return entities directly** - use DTOs via mappers
5. **ALWAYS use CommissionCalculationService** for financial calculations

### ⛔ **DON'T Do This:**

```java
// ❌ WRONG - No transaction
public PlacementResponse create(CreatePlacementRequest request) {
    Placement placement = mapper.toEntity(request);
    return mapper.toResponse(repository.save(placement));
}

// ❌ WRONG - Not validating related entities
Salesperson salesperson = salespersonRepository.findById(id).get();

// ❌ WRONG - Manual calculation
BigDecimal commission = netMargin.multiply(new BigDecimal("0.15"));
placement.setCommissionTotal(commission);

// ❌ WRONG - Returning entity
public Placement getById(Long id) {
    return repository.findById(id).orElseThrow();
}
```

### ✅ **DO This:**

```java
// ✅ CORRECT - Transactional with proper validation
@Override
@Transactional
public PlacementResponse create(CreatePlacementRequest request) {
    log.info("Creating placement for contractor ID: {}, client ID: {}",
             request.getContractorId(), request.getClientId());

    // Validate all related entities exist
    Salesperson salesperson = salespersonRepository.findById(request.getSalespersonId())
        .orElseThrow(() -> new ResourceNotFoundException("Salesperson", "id", request.getSalespersonId()));

    Client client = clientRepository.findById(request.getClientId())
        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

    Contractor contractor = contractorRepository.findById(request.getContractorId())
        .orElseThrow(() -> new ResourceNotFoundException("Contractor", "id", request.getContractorId()));

    // Get settings
    List<PolicySettings> allSettings = settingsRepository.findAll();
    PolicySettings settings = allSettings.isEmpty() ? null : allSettings.get(0);

    // Create entity
    Placement entity = mapper.toEntity(request);
    entity.setSalesperson(salesperson);
    entity.setClient(client);
    entity.setContractor(contractor);

    // Calculate sequence number
    int sequenceNumber = repository.countByContractorAndClient(contractor, client) + 1;
    entity.setSequenceNumber(sequenceNumber);

    // Set defaults from settings
    if (settings != null && entity.getPlacementType() == PlacementType.CONTRACTOR) {
        entity.setAdminPercentage(settings.getAdminPercentage());
        entity.setInsurancePercentage(settings.getInsurancePercentage());
    }

    // Calculate commission
    if (entity.getPlacementType() == PlacementType.CONTRACTOR) {
        calculationService.calculateContractorCommission(entity, settings);
    } else if (entity.getPlacementType() == PlacementType.PERMANENT) {
        calculationService.calculatePermanentCommission(entity, settings);
    }

    // Save and return DTO
    Placement saved = repository.save(entity);
    log.info("Placement created successfully with ID: {}", saved.getId());

    return mapper.toResponse(saved);
}

// ✅ CORRECT - Return DTO
public PlacementResponse getById(Long id) {
    Placement entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Placement", "id", id));
    return mapper.toResponse(entity);
}
```

### **Service Method Template:**

```java
@Override
@Transactional  // If modifying data
public EntityResponse methodName(RequestDTO request) {
    // 1. Log the operation
    log.info("Operation description with key params: {}", param);

    // 2. Validate all related entities exist
    RelatedEntity entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Entity", "id", id));

    // 3. Check business rules
    if (businessRuleViolated()) {
        throw new BusinessRuleException("Clear error message");
    }

    // 4. Perform operation
    Entity result = // ... operation

    // 5. Save if needed
    Entity saved = repository.save(result);

    // 6. Log success
    log.info("Operation completed successfully: {}", saved.getId());

    // 7. Return DTO
    return mapper.toResponse(saved);
}
```

---

## 🌐 API Endpoint Changes

### **API Design Rules:**

1. **ALWAYS use standard HTTP methods** - GET, POST, PUT, DELETE
2. **ALWAYS return consistent response format** - Use ApiResponse wrapper
3. **ALWAYS use @Valid** for request body validation
4. **ALWAYS document with @Operation** (Swagger)
5. **ALWAYS use proper HTTP status codes**

### ⛔ **DON'T Do This:**

```java
// ❌ WRONG - No validation
@PostMapping
public PlacementResponse create(@RequestBody CreatePlacementRequest request) {

// ❌ WRONG - Returning entity directly
@GetMapping("/{id}")
public Placement getById(@PathVariable Long id) {

// ❌ WRONG - Wrong HTTP method
@GetMapping("/delete/{id}")
public void delete(@PathVariable Long id) {

// ❌ WRONG - No API documentation
@PostMapping
public ResponseEntity<PlacementResponse> create(...) {

// ❌ WRONG - Generic exception handling in controller
try {
    service.create(request);
} catch (Exception e) {
    return ResponseEntity.status(500).build();
}
```

### ✅ **DO This:**

```java
// ✅ CORRECT - Validation + Documentation
@PostMapping
@Operation(summary = "Create a new placement",
           description = "Creates a new placement record with automatic commission calculation")
public ResponseEntity<ApiResponse<PlacementResponse>> create(
        @Valid @RequestBody CreatePlacementRequest request) {

    PlacementResponse created = placementService.create(request);

    ApiResponse<PlacementResponse> response = ApiResponse.<PlacementResponse>builder()
            .success(true)
            .message("Placement created successfully")
            .data(created)
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// ✅ CORRECT - Return DTO with proper wrapper
@GetMapping("/{id}")
@Operation(summary = "Get placement by ID")
public ResponseEntity<ApiResponse<PlacementResponse>> getById(@PathVariable Long id) {
    PlacementResponse placement = placementService.getById(id);

    ApiResponse<PlacementResponse> response = ApiResponse.<PlacementResponse>builder()
            .success(true)
            .message("Placement retrieved successfully")
            .data(placement)
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.ok(response);
}

// ✅ CORRECT - Proper HTTP method
@DeleteMapping("/{id}")
@Operation(summary = "Delete placement by ID")
public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
    placementService.delete(id);

    ApiResponse<Void> response = ApiResponse.<Void>builder()
            .success(true)
            .message("Placement deleted successfully")
            .data(null)
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.ok(response);
}
```

### **Endpoint Standards:**

| Operation | HTTP Method | Endpoint Pattern | Response Code |
|-----------|-------------|------------------|---------------|
| Create | POST | `/api/entities` | 201 Created |
| Get All | GET | `/api/entities` | 200 OK |
| Get One | GET | `/api/entities/{id}` | 200 OK |
| Update | PUT | `/api/entities/{id}` | 200 OK |
| Delete | DELETE | `/api/entities/{id}` | 200 OK |
| Filter | GET | `/api/entities/status/{status}` | 200 OK |

### **Error Response Standards:**

Let GlobalExceptionHandler handle all exceptions. DO NOT catch exceptions in controllers.

```java
// ✅ CORRECT - Let exception propagate
@PostMapping
public ResponseEntity<ApiResponse<PlacementResponse>> create(
        @Valid @RequestBody CreatePlacementRequest request) {
    PlacementResponse created = placementService.create(request);
    // No try-catch needed
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

## 🧪 Testing Requirements

### **MANDATORY Testing Rules:**

1. **ALWAYS test calculations with known expected values**
2. **ALWAYS test all CRUD operations**
3. **ALWAYS test validation rules**
4. **ALWAYS test edge cases**
5. **NEVER commit code with failing tests**

### **Financial Calculation Testing:**

Every calculation change MUST have tests that verify:

```java
@Test
void testCalculationWithKnownValues() {
    // ✅ CORRECT - Test with exact expected values
    BigDecimal input = new BigDecimal("55000");
    BigDecimal expected = new BigDecimal("35.08");

    BigDecimal result = calculationService.calculateHourlyPayCost(
        input, hoursPerWeek, settings);

    assertThat(result).isEqualByComparingTo(expected);
}

@Test
void testSequenceCommissionPercentages() {
    // Test 1st contract
    assertThat(calculationService.determineCommissionPercentage(1, settings))
        .isEqualByComparingTo(new BigDecimal("15.00"));

    // Test 2nd contract
    assertThat(calculationService.determineCommissionPercentage(2, settings))
        .isEqualByComparingTo(new BigDecimal("10.00"));

    // Test 3rd+ contracts
    assertThat(calculationService.determineCommissionPercentage(3, settings))
        .isEqualByComparingTo(new BigDecimal("8.00"));
    assertThat(calculationService.determineCommissionPercentage(10, settings))
        .isEqualByComparingTo(new BigDecimal("8.00"));
}
```

### **Test Coverage Requirements:**

Minimum coverage for each component:

- **Services**: 80% minimum
  - All business logic paths
  - All validation rules
  - All error conditions

- **Calculations**: 100% required
  - Every calculation method
  - Edge cases (zero, negative, very large numbers)
  - Rounding verification

- **Controllers**: 70% minimum
  - Happy path
  - Validation failures
  - Not found scenarios

### **Before Each Commit:**

```bash
# Run all tests
./mvnw clean test

# Verify calculations specifically
./mvnw test -Dtest=CommissionCalculationServiceTest

# Check compilation
./mvnw clean compile
```

---

## ⚠️ Common Mistakes to Avoid

### **❌ DON'T Do This:**

#### 1. Financial Calculations
- ❌ Use `float` or `double` for money
- ❌ Perform calculations without specifying scale and rounding
- ❌ Manually set calculated fields
- ❌ Hardcode commission percentages or overhead rates
- ❌ Skip validation of calculation inputs

#### 2. Entity Management
- ❌ Delete entities without checking dependencies
- ❌ Use EAGER fetching by default
- ❌ Skip validation on related entities
- ❌ Allow null values on required fields
- ❌ Expose entities directly via API

#### 3. Database Operations
- ❌ Modify existing migration files
- ❌ Use JPA ddl-auto=update in production
- ❌ Create tables without indexes on foreign keys
- ❌ Use VARCHAR for financial data
- ❌ Delete data without backup/soft delete option

#### 4. Service Layer
- ❌ Skip @Transactional on data-modifying methods
- ❌ Return entities instead of DTOs
- ❌ Catch and swallow exceptions
- ❌ Skip logging of important operations
- ❌ Perform calculations inline instead of using service

#### 5. API Design
- ❌ Skip input validation with @Valid
- ❌ Use wrong HTTP methods (GET for delete, etc.)
- ❌ Return inconsistent response formats
- ❌ Skip API documentation annotations
- ❌ Handle exceptions in controller

#### 6. Testing
- ❌ Skip testing financial calculations
- ❌ Test without expected values
- ❌ Commit code with failing tests
- ❌ Skip edge case testing
- ❌ Mock calculation services in calculation tests

### **✅ DO This:**

#### 1. Financial Calculations
- ✅ Always use `BigDecimal` for all money and percentage values
- ✅ Always specify `.setScale(2, RoundingMode.HALF_UP)`
- ✅ Always use CommissionCalculationService
- ✅ Always get rates from PolicySettings
- ✅ Always validate inputs are not null and >= 0

#### 2. Entity Management
- ✅ Use status fields for soft deletion
- ✅ Default to LAZY fetching
- ✅ Validate all foreign key entities exist before saving
- ✅ Use @NotNull, @NotBlank appropriately
- ✅ Map entities to DTOs before returning

#### 3. Database Operations
- ✅ Create new migration for every schema change
- ✅ Use Flyway for all schema management
- ✅ Create indexes on all foreign keys
- ✅ Use DECIMAL(12,2) for financial data
- ✅ Implement soft delete with status field

#### 4. Service Layer
- ✅ Use @Transactional on all write operations
- ✅ Return DTOs via mappers
- ✅ Let exceptions propagate to GlobalExceptionHandler
- ✅ Log at INFO for important events, DEBUG for details
- ✅ Use dedicated calculation service

#### 5. API Design
- ✅ Use @Valid on all @RequestBody parameters
- ✅ Follow REST conventions for HTTP methods
- ✅ Use ApiResponse wrapper for consistency
- ✅ Document with @Operation annotations
- ✅ Let GlobalExceptionHandler manage errors

#### 6. Testing
- ✅ Test all calculations with expected values
- ✅ Use assertThat().isEqualByComparingTo() for BigDecimal
- ✅ Run full test suite before committing
- ✅ Test null, zero, negative, and boundary values
- ✅ Use real service instances for integration tests

---

## 📁 Files to Review Checklist

### **When Modifying Financial Calculations:**

**ALWAYS review these files in order:**

1. **PolicySettings.java**
   - `src/main/java/com/ContractBilling/commissions/entity/PolicySettings.java`
   - ⚠️ Check all percentage fields
   - ⚠️ Verify commission tier values
   - ⚠️ Confirm weeks per year setting

2. **CommissionCalculationService.java** (Interface)
   - `src/main/java/com/ContractBilling/commissions/service/CommissionCalculationService.java`
   - ⚠️ Update method signatures if needed
   - ⚠️ Update JavaDoc with new formulas

3. **CommissionCalculationServiceImpl.java** (Implementation)
   - `src/main/java/com/ContractBilling/commissions/service/impl/CommissionCalculationServiceImpl.java`
   - ⚠️ Modify calculation logic
   - ⚠️ Update rounding/scale constants if needed
   - ⚠️ Ensure BigDecimal operations are correct

4. **PlacementServiceImpl.java**
   - `src/main/java/com/ContractBilling/commissions/service/impl/PlacementServiceImpl.java`
   - ⚠️ Check calculation trigger logic (lines 76-81)
   - ⚠️ Check recalculation logic (lines 135-155)
   - ⚠️ Verify settings retrieval

5. **Placement.java** (Entity)
   - `src/main/java/com/ContractBilling/commissions/entity/Placement.java`
   - ⚠️ Check calculated field definitions
   - ⚠️ Verify column precision/scale (DECIMAL)
   - ⚠️ Ensure no public setters on calculated fields

6. **CommissionCalculationServiceTest.java**
   - `src/test/java/com/ContractBilling/commissions/service/CommissionCalculationServiceTest.java`
   - ⚠️ Update expected values
   - ⚠️ Add tests for new scenarios
   - ⚠️ Verify all 16 tests pass

7. **Database Migration** (Create new one)
   - `src/main/resources/db/migration/V7__*.sql`
   - ⚠️ If schema changes needed

### **When Adding New Entity:**

**Follow this checklist:**

1. [ ] Create Entity class in `entity/` package
   - Add @Entity, @Table annotations
   - Define all fields with proper types
   - Add relationships with LAZY fetch
   - Add audit timestamps
   - Use BigDecimal for financial fields

2. [ ] Create Enum classes if needed in `entity/` package
   - Define all possible values

3. [ ] Create DTOs in `dto/` package
   - CreateRequest (input validation)
   - UpdateRequest (partial updates)
   - Response (output format)

4. [ ] Create Mapper in `dto/` package
   - toEntity(CreateRequest)
   - updateEntity(Entity, UpdateRequest)
   - toResponse(Entity)

5. [ ] Create Repository in `repository/` package
   - Extend JpaRepository
   - Add custom query methods if needed

6. [ ] Create Service interface in `service/` package
   - Define all CRUD methods
   - Define filter methods

7. [ ] Create Service implementation in `service/impl/` package
   - Implement all methods
   - Add @Transactional
   - Add validation logic
   - Add logging

8. [ ] Create Controller in `controller/` package
   - Add all REST endpoints
   - Use @Valid on inputs
   - Wrap responses in ApiResponse
   - Add @Operation documentation

9. [ ] Create Flyway migration in `db/migration/`
   - Create table
   - Add indexes
   - Add foreign keys

10. [ ] Create Tests in `test/` package
    - Service tests
    - Repository tests (if custom queries)
    - Integration tests

11. [ ] Update Documentation
    - Update claude.md
    - Update API documentation

### **When Modifying Existing Entity:**

**Required Reviews:**

1. [ ] Entity class - Check field changes
2. [ ] All DTOs - Update affected fields
3. [ ] Mapper - Update mapping logic
4. [ ] Service - Update business logic
5. [ ] Controller - Update API contracts
6. [ ] Migration - Create ALTER TABLE script
7. [ ] Tests - Update assertions
8. [ ] Documentation - Update API docs

### **When Changing Business Rules:**

**Critical Files:**

1. **PolicySettings** - If default values change
2. **Service Implementation** - Where rules are enforced
3. **Calculation Service** - If calculation logic changes
4. **Tests** - Update expected values
5. **Migration** - If default data changes

---

## 💡 Commission Calculation Rules Reference

### **Sequence Number Calculation:**

```java
// ALWAYS use this pattern
int sequenceNumber = placementRepository
    .countByContractorAndClient(contractor, client) + 1;

// Sequence determines commission percentage:
// 1 → 15% (first contract with this client)
// 2 → 10% (second contract with this client)
// 3+ → 8% (third and all subsequent contracts)
```

### **Contractor Commission Calculation Chain:**

```
1. Hourly Pay Cost = (Annual Salary ÷ 52 ÷ Hours/Week)
                      × (1 + Leave%)
                      × (1 + PRSI%)
                      × (1 + Pension%)

2. Margin Per Hour = Bill Rate - Hourly Pay Cost

3. Weekly Margin = Margin Per Hour × Hours Per Week

4. Gross Annual Margin = Weekly Margin × Weeks Per Year

5. Overheads = (Admin% × Gross) + (Insurance% × Gross) + Fixed Costs

6. Net Annual Margin = Gross Annual Margin - Overheads

7. Commission Percentage = Determined by Sequence Number

8. Commission Total = Net Annual Margin × (Commission% ÷ 100)
```

### **Permanent Placement Commission:**

```
1. Net Margin = Placement Fee (no overhead calculation)

2. Commission Percentage = Determined by Sequence Number

3. Commission Total = Net Margin × (Commission% ÷ 100)
```

### **When to Recalculate:**

Recalculate commission if ANY of these change:
- `annualSalary`
- `hourlyPayRate`
- `billRate`
- `hoursPerWeek`
- `weeksPerYear`
- `placementFee`

### **PolicySettings Usage:**

NEVER hardcode these values - ALWAYS get from PolicySettings:
- Leave percentage (default: 14.54%)
- PRSI percentage (default: 11.25%)
- Pension percentage (default: 1.5%)
- Admin percentage (default: 6.0%)
- Insurance percentage (default: 2.0%)
- Weeks per year (default: 45)
- First contract commission (default: 15.0%)
- Second contract commission (default: 10.0%)
- Third contract commission (default: 8.0%)

---

## 🚀 Before Every Release

### **Pre-Release Checklist:**

#### Code Quality
- [ ] All tests pass (`./mvnw clean test`)
- [ ] No compiler warnings
- [ ] Code review completed
- [ ] Logging is appropriate (no DEBUG in production)

#### Financial Accuracy
- [ ] All calculations tested with known values
- [ ] Sequence number calculation verified
- [ ] Commission percentages match PolicySettings
- [ ] Rounding is HALF_UP everywhere

#### Database
- [ ] All migrations tested
- [ ] Rollback scripts prepared
- [ ] Indexes created on new columns
- [ ] Backup taken before schema changes

#### API
- [ ] Swagger documentation updated
- [ ] All endpoints tested manually
- [ ] Error responses are consistent
- [ ] Breaking changes documented

#### Documentation
- [ ] API documentation updated
- [ ] claude.md updated if architecture changed
- [ ] DEVELOPMENT_GUIDELINES.md updated if rules changed
- [ ] README updated if setup changed

---

## 🆘 Emergency Procedures

### **If Calculation Error Discovered:**

1. **STOP** - Immediately halt any further placements using wrong calculations
2. **IDENTIFY** - Determine which placements are affected
   ```sql
   -- Example query to find affected records
   SELECT id, commission_total, created_at
   FROM placement
   WHERE created_at >= '2025-11-11'
   AND commission_total IS NOT NULL;
   ```
3. **FIX** - Correct the calculation service
4. **TEST** - Verify fix with known values
5. **RECALCULATE** - Update affected records
   ```java
   // Recalculation script
   List<Placement> affected = placementRepository.findAll();
   for (Placement p : affected) {
       calculationService.calculateContractorCommission(p, settings);
       placementRepository.save(p);
   }
   ```
6. **AUDIT** - Log all recalculated records
7. **NOTIFY** - Inform stakeholders of changes

### **If Database Migration Fails:**

1. **CHECK** Flyway schema history
   ```sql
   SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;
   ```
2. **REPAIR** if needed
   ```bash
   ./mvnw flyway:repair
   ```
3. **ROLLBACK** if necessary (use rollback script from migration)
4. **FIX** migration file
5. **TEST** on dev/staging first
6. **RETRY** migration

---

## 📞 Questions or Issues?

If you're unsure about any of these guidelines:

1. **READ** this document again carefully
2. **CHECK** the claude.md for technical reference
3. **REVIEW** existing code for patterns
4. **TEST** your changes thoroughly
5. **ASK** for clarification before proceeding

---

## ✍️ Document Maintenance

This document MUST be updated when:
- New calculation rules are added
- Business logic changes
- New entities are introduced
- API patterns change
- Common mistakes are discovered

**Last Updated:** 2025-11-11
**Version:** 1.0
**Next Review:** Before starting Day 9

---

**⚠️ REMEMBER: Financial accuracy is paramount. When in doubt, TEST with known expected values!**
