# Commission Management System - API Testing Guide

## Quick Start

### 1. Start the Application

```bash
./mvnw spring-boot:run
```

Wait for the message: `Started CommissionsApplication in X seconds`

### 2. Access Swagger UI

Open your browser and go to:
```
http://localhost:8080/swagger-ui.html
```

---

## Authentication Setup

All endpoints (except registration and login) require authentication.

### Step 1: Register a User

1. In Swagger UI, find the **Authentication** section
2. Click on **POST** `/api/auth/register`
3. Click **"Try it out"**
4. Replace the request body with:

```json
{
  "username": "demo_user",
  "email": "demo@example.com",
  "password": "demo123",
  "firstName": "Demo",
  "lastName": "User"
}
```

5. Click **"Execute"**
6. **Copy the token** from the response (starts with `eyJ...`)

### Step 2: Authorize All Requests

1. Click the green **"Authorize"** button at the top right
2. In the popup, enter: `Bearer <paste-your-token-here>`
   - Example: `Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJkZW1vX3VzZXIi...`
3. Click **"Authorize"**
4. Click **"Close"**

**Note:** After any server restart or page refresh, you must click "Authorize" again!

---

## Complete Testing Workflow

### Test 1: Create Basic Entities

#### A. Create a Salesperson

**Endpoint:** POST `/api/salespeople`

```json
{
  "name": "Alice Johnson",
  "email": "alice@sales.com",
  "phone": "+1234567890",
  "status": "ACTIVE"
}
```

**Expected Response:** 200 OK with salesperson ID (note this ID for later)

#### B. Create a Client

**Endpoint:** POST `/api/clients`

```json
{
  "name": "Acme Corporation",
  "email": "contact@acme.com",
  "phone": "+1987654321",
  "address": "123 Business Street",
  "contactPerson": "Bob Smith",
  "status": "ACTIVE"
}
```

**Expected Response:** 200 OK with client ID

#### C. Create a Contractor

**Endpoint:** POST `/api/contractors`

```json
{
  "name": "Jane Developer",
  "email": "jane@contractor.com",
  "phone": "+1122334455",
  "type": "CONTRACTOR",
  "status": "ACTIVE"
}
```

**Expected Response:** 200 OK with contractor ID

---

### Test 2: Create First Placement (15% Commission)

**Endpoint:** POST `/api/placements`

```json
{
  "salespersonId": 1,
  "clientId": 1,
  "contractorId": 1,
  "placementType": "CONTRACTOR",
  "status": "ACTIVE",
  "startDate": "2025-01-15",
  "endDate": "2025-12-31",
  "hoursPerWeek": 40,
  "weeksPerYear": 45,
  "payType": "SALARY",
  "annualSalary": 60000,
  "billRate": 45.50
}
```

**What to Look For:**
- ✅ `commissionTotal`: ~$2,034.40 (auto-calculated!)
- ✅ `commissionPercentage`: 15% (first contract)
- ✅ `sequenceNumber`: 1
- ✅ `netAnnualMargin`: ~$13,562.64
- ✅ `status`: "DRAFT"

**Behind the Scenes:** This automatically creates:
1. Commission Plan (ID: 1)
2. Revenue Recognition Schedule (12 monthly entries)
3. Ledger entries

---

### Test 3: View Commission Plan

**Endpoint:** GET `/api/commission-plans/placement/1`

(Use the placement ID from previous response)

**What to Look For:**
- `plannedAmount`: 2034.40
- `status`: "PLANNED"
- `monthsToRecognize`: 12
- `monthsRecognized`: 0

---

### Test 4: View Revenue Recognition Schedule

**Endpoint:** GET `/api/recognition/plan/1`

(Use the commission plan ID)

**What to Look For:**
- 12 entries (one per month)
- Each entry: `plannedAmount`: ~169.53 ($2,034.40 ÷ 12)
- `status`: "PENDING"
- Recognition dates spanning 12 months

---

### Test 5: View Salesperson Dashboard

**Endpoint:** GET `/api/reports/salesperson/1/dashboard`

**What to Look For:**
```json
{
  "totalPlanned": 2034.40,
  "totalRecognized": 0,
  "totalPaid": 0,
  "availableBalance": 0,
  "placements": [...],
  "commissionBreakdown": [...]
}
```

---

### Test 6: Create Second Placement (10% Commission)

Test the tiered commission logic!

**Endpoint:** POST `/api/placements`

```json
{
  "salespersonId": 1,
  "clientId": 1,
  "contractorId": 1,
  "placementType": "CONTRACTOR",
  "status": "ACTIVE",
  "startDate": "2026-01-15",
  "endDate": "2026-12-31",
  "hoursPerWeek": 40,
  "weeksPerYear": 45,
  "payType": "SALARY",
  "annualSalary": 65000,
  "billRate": 48.00
}
```

**What to Look For:**
- ✅ `commissionPercentage`: **10%** (second contract - lower rate!)
- ✅ `sequenceNumber`: **2**
- ✅ Lower commission total than first placement

---

### Test 7: Create Third Placement (8% Commission)

**Endpoint:** POST `/api/placements`

Same as Test 6, but change:
- `startDate`: "2027-01-15"
- `endDate`: "2027-12-31"
- `annualSalary`: 70000

**What to Look For:**
- ✅ `commissionPercentage`: **8%** (third contract - lowest rate!)
- ✅ `sequenceNumber`: **3**

---

### Test 8: Test Permanent Placement

Different calculation method!

**Endpoint:** POST `/api/placements`

```json
{
  "salespersonId": 1,
  "clientId": 1,
  "contractorId": 2,
  "placementType": "PERMANENT",
  "status": "ACTIVE",
  "startDate": "2025-03-01",
  "placementFee": 15000,
  "feeType": "FLAT",
  "candidateSalary": 75000,
  "recognitionPeriodMonths": 12
}
```

**Note:** You'll need to create a second contractor first!

**What to Look For:**
- `commissionTotal`: 2,250 (15% of $15,000)
- `sequenceNumber`: 1 (different contractor)
- Simpler calculation than contractor placements

---

### Test 9: View Commission Breakdown

**Endpoint:** GET `/api/reports/salesperson/1/commissions`

**What to Look For:**
- List of all placements with commission details
- Different percentages (15%, 10%, 8%)
- Total commissions

---

### Test 10: View Ledger (Audit Trail)

**Endpoint:** GET `/api/ledger/salesperson/1`

**What to Look For:**
- `COMMISSION_ACCRUED` entries for each placement
- Complete transaction history
- Timestamps for audit compliance

---

### Test 11: View System Health

**Endpoint:** GET `/api/reports/health`

**What to Look For:**
```json
{
  "totalPlacements": 3,
  "totalSalespeople": 1,
  "totalClients": 1,
  "totalCommissionsPlanned": X,
  "systemStatus": "HEALTHY"
}
```

---

### Test 12: Top Performers Report

**Endpoint:** GET `/api/reports/top-performers?limit=5`

**What to Look For:**
- Ranked list of salespeople by commission
- Total earnings per salesperson

---

## Testing Revenue Recognition

To test revenue recognition (requires time manipulation or manual trigger):

**Endpoint:** POST `/api/recognition/process`

**Query Parameter:**
- `asOfDate`: `2025-02-01`

This processes all due recognition schedules.

---

## Testing Drawdown Requests

### Check Available Balance

**Endpoint:** GET `/api/drawdowns/salesperson/1/available`

**Note:** Balance may be $0 until revenue is recognized!

### Request Drawdown

**Endpoint:** POST `/api/drawdowns`

**Query Parameters:**
- `salespersonId`: 1
- `amount`: 500

**Expected:** May fail if no revenue recognized yet (business rule)

### View Drawdown History

**Endpoint:** GET `/api/drawdowns/salesperson/1`

---

## Common Pagination Parameters

For list endpoints, use:
- `page`: 0 (first page, **zero-indexed!**)
- `size`: 20 (items per page)
- `sort`: `createdAt,desc` (optional)

**Example:**
```
GET /api/placements?page=0&size=20&sort=createdAt,desc
```

---

## Troubleshooting

### Error: 403 Forbidden
**Solution:** Click "Authorize" button and paste your token again

### Error: 400 Bad Request
**Solution:** Check your JSON - ensure valid email format, required fields, etc.

### Error: 404 Not Found
**Solution:** Entity ID doesn't exist - verify IDs from previous responses

### Error: 500 Internal Server Error
**Solution:** Check server logs for detailed error message

---

## Key Features Demonstrated

✅ **Automatic Commission Calculation** - Complex multi-step financial math
✅ **Tiered Commission Rates** - 15% → 10% → 8% based on sequence
✅ **Revenue Recognition** - 12-month amortization scheduling
✅ **Audit Trail** - Complete ledger of all transactions
✅ **Comprehensive Reporting** - Dashboards, breakdowns, summaries
✅ **JWT Authentication** - Secure API access
✅ **RESTful Design** - Standard HTTP methods and status codes

---

## Sample Demo Script for Client

### 5-Minute Demo Flow:

1. **Show Swagger UI** - "Here's your interactive API documentation"
2. **Register & Authenticate** - "All endpoints are secured with JWT"
3. **Create Entities** - "Let's set up a salesperson, client, and contractor"
4. **Create First Placement** - "Watch the system calculate $2,034.40 commission automatically!"
5. **Show Dashboard** - "Complete financial overview in real-time"
6. **Create Second Placement** - "Notice the commission rate dropped to 10%"
7. **View Ledger** - "Full audit trail for compliance"
8. **Show Recognition Schedule** - "Revenue amortized over 12 months"

**Total time:** 5-10 minutes
**Impact:** High - shows all key features working end-to-end

---

## Next Steps

1. **Deployment** - See `DEPLOYMENT.md` for production setup
2. **Frontend Integration** - Use Swagger's "Try it out" curl commands as reference
3. **Database Backup** - Regular PostgreSQL backups recommended
4. **Monitoring** - Set up logging and alerts for production

---

## Support

For questions or issues:
- Review `PROJECT_SUMMARY.md` for technical details
- Check `DEPLOYMENT.md` for setup instructions
- Contact development team for custom enhancements

---

**Last Updated:** 2025-11-13
**Version:** 1.0
**Status:** Production Ready ✅
