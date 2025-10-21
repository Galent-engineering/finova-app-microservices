# OLTP & OLAP Integration - Implementation Guide

This document describes the implementation of OLTP (Payment Service) and OLAP (Analytics Service) features for the Finova Retirement Microservices application.

## Overview

Two new microservices have been added to demonstrate real-time transaction processing (OLTP) and analytical reporting (OLAP) using MongoDB:

1. **Payment Service (OLTP)** - Port 8084
2. **Analytics Service (OLAP)** - Port 8085

## Architecture Changes

### New Services

#### Payment Service (OLTP)
- **Purpose**: Handle real-time advisory subscription payments with immediate confirmation
- **Database**: MongoDB (payment_db)
- **Port**: 8084
- **Key Features**:
  - Advisory subscription management (Basic, Plus, Premium tiers)
  - Billing frequency options (Monthly, Quarterly, Annual with discounts)
  - Payment processing with mock gateway (90% success rate)
  - Payment method management
  - Real-time transaction processing (<2 seconds)
  - Automatic retry mechanism for failed payments
  - Subscription pause/cancel functionality

#### Analytics Service (OLAP)
- **Purpose**: Generate insights and reports from historical retirement account data
- **Database**: MongoDB (analytics_db)
- **Port**: 8085
- **Key Features**:
  - 6 months of auto-generated historical data
  - Real-time dashboard with key metrics
  - Interactive charts and visualizations
  - Quarterly performance analysis
  - Contribution breakdown analysis
  - Account distribution insights
  - Actionable recommendations

### MongoDB Integration

MongoDB 7.0 has been added to the Docker Compose stack:
- **Container**: finova-mongodb
- **Port**: 27017
- **Credentials**: admin/password
- **Databases**: payment_db, analytics_db

## API Endpoints

### Payment Service

#### Subscriptions
```
GET    /api/subscriptions/{userId}              - Get all subscriptions for user
GET    /api/subscriptions/{userId}/active       - Get active subscription
POST   /api/subscriptions                       - Create new subscription
PUT    /api/subscriptions/{id}                  - Update subscription
DELETE /api/subscriptions/{id}                  - Cancel subscription
PATCH  /api/subscriptions/{id}/pause            - Pause subscription
PUT    /api/subscriptions/{id}/payment-method   - Update payment method
```

#### Payments
```
POST   /api/payments/process                    - Process payment
GET    /api/payments/history/{userId}           - Get payment history
POST   /api/payments/{paymentId}/retry          - Retry failed payment
```

### Analytics Service

```
GET    /api/analytics/dashboard/{userId}?period={3m|6m|12m|all}
```

Returns comprehensive dashboard data:
- Key stats (Total Assets, Annual Contribution, YTD Return, On Track Score)
- Savings growth over time (actual vs target)
- Contribution breakdown (employee, employer, previous balance)
- Account breakdown by type
- Quarterly returns
- Actionable insights

## Frontend Pages

### 1. Manage Billing (`payments.html`)
Located at: `http://localhost:[port]/payments.html`

**Features**:
- View active advisory subscription
- Billing frequency and amount display
- Next payment date
- Payment method information
- Change payment method (modal)
- Update billing frequency (modal)
- Pause or cancel subscription (modal)
- Payment history with transaction details

**Screenshots from Figma**:
- Payment Dashboard
- Change Payment Method
- Update Frequency
- Cancel/Pause Subscription
- Success Confirmation

### 2. Analytics Dashboard (`analytics.html`)
Located at: `http://localhost:[port]/analytics.html`

**Features**:
- Time period selector (3M, 6M, 12M, All)
- Export functionality
- 4 key stat cards with trend indicators
- Savings Growth Line Chart (actual vs target balance)
- Annual Contribution Breakdown (donut chart)
- Account Breakdown (bar chart)
- Quarterly Returns display
- 3 actionable insights cards

**Technology**:
- Chart.js 4.4.0 for visualizations
- Responsive grid layout
- Real-time data updates

### 3. Updated Main Dashboard
- Added "Manage Billing" and "Analytics" navigation links
- Added quick action cards for both new features

## Data Models

### Payment Service Models

#### Subscription
```java
- id: String
- userId: Long
- tier: SubscriptionTier (BASIC, PLUS, PREMIUM)
- frequency: BillingFrequency (MONTHLY, QUARTERLY, ANNUAL)
- amount: Double
- status: SubscriptionStatus (ACTIVE, PAUSED, CANCELLED)
- paymentMethod: PaymentMethod
- nextPaymentDate: LocalDateTime
- startDate/endDate: LocalDateTime
```

#### Payment
```java
- id: String
- subscriptionId: String
- userId: Long
- amount: Double
- status: PaymentStatus (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED)
- transactionId: String
- retryCount: Integer
- errorMessage: String
```

### Analytics Service Models

#### AccountSnapshot
```java
- userId: Long
- accountType: AccountType (TOTAL, K401, IRA_TRADITIONAL, IRA_ROTH, BROKERAGE)
- snapshotDate: LocalDate
- balance: Double
- targetBalance: Double
- monthlyContribution: Double
- returnRate: Double
```

#### ContributionHistory
```java
- userId: Long
- contributionDate: LocalDate
- type: ContributionType (EMPLOYEE_PRETAX, EMPLOYEE_ROTH, EMPLOYER_MATCH)
- amount: Double
- accountType: String
```

#### PerformanceMetric
```java
- userId: Long
- periodStart/periodEnd: LocalDate
- period: Period (MONTHLY, QUARTERLY, ANNUAL)
- returnPercentage: Double
- beginningBalance/endingBalance: Double
- totalContributions/totalGains: Double
```

## Running the Application

### 1. Start MongoDB and Services

```bash
# From project root
docker-compose up -d mongodb

# Start services individually or via docker-compose
mvn clean install
mvn spring-boot:run -pl payment-service
mvn spring-boot:run -pl analytics-service
```

### 2. Start Frontend

```bash
cd frontend
# Open in browser or use a local server
python -m http.server 8000
# Navigate to http://localhost:8000
```

### 3. Access the Application

- **Main Dashboard**: http://localhost:8000/index.html
- **Manage Billing**: http://localhost:8000/payments.html
- **Analytics**: http://localhost:8000/analytics.html

### 4. API Gateway Routes

The API Gateway (port 8080) routes requests:
- `/api/subscriptions/**` → payment-service:8084
- `/api/payments/**` → payment-service:8084
- `/api/analytics/**` → analytics-service:8085

## Sample Data

The Analytics Service automatically generates 6 months of sample data on startup:
- Monthly account snapshots for all account types
- Contribution history (employee + employer match)
- Quarterly performance metrics
- Realistic growth patterns (~7% annual return)

Data is generated for User ID 1 by default.

## Mock Payment Gateway

The Payment Service includes a mock payment gateway that:
- Simulates processing delay (500-1500ms)
- 90% success rate
- Random error messages for failures
- Generates transaction IDs for successful payments
- Supports retry mechanism

## Key Features Implemented

### OLTP Requirements (GAP-133)
✅ Real-time payment processing (<2 seconds)
✅ Advisory subscription management (3 tiers, 3 frequencies)
✅ Payment method updates
✅ Immediate confirmation
✅ Comprehensive error handling with retry
✅ Pause/Cancel functionality
✅ Payment history tracking

### OLAP Requirements (GAP-132)
✅ MongoDB aggregation pipelines
✅ Historical data (6 months)
✅ Time-based reporting (3m, 6m, 12m, all)
✅ Analytics dashboard with key stats
✅ Interactive charts (Line, Donut, Bar)
✅ Quarterly performance metrics
✅ Actionable insights generation
✅ Account breakdown visualization

## Testing

### Payment Service

```bash
# Get active subscription
curl http://localhost:8084/api/subscriptions/1/active

# Create subscription
curl -X POST http://localhost:8084/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "tier": "PREMIUM",
    "frequency": "MONTHLY",
    "paymentMethod": {
      "type": "CREDIT_CARD",
      "last4": "1234",
      "brand": "Visa",
      "holderName": "John Doe"
    }
  }'

# Get payment history
curl http://localhost:8084/api/payments/history/1
```

### Analytics Service

```bash
# Get dashboard data
curl "http://localhost:8085/api/analytics/dashboard/1?period=12m"
```

## Technology Stack

### Backend
- Spring Boot 3.1.5
- Spring Data MongoDB
- MongoDB 7.0
- Project Lombok

### Frontend
- Vanilla JavaScript
- Chart.js 4.4.0
- Font Awesome 6.0
- CSS Grid & Flexbox

### Infrastructure
- Docker & Docker Compose
- Spring Cloud Gateway
- Eureka Service Discovery

## Performance Characteristics

### OLTP (Payment Service)
- Average response time: <2 seconds
- Transaction throughput: Optimized for individual transactions
- Consistency: Strong (MongoDB ACID transactions)
- Availability: High (with retry mechanism)

### OLAP (Analytics Service)
- Query response time: <1 second for dashboards
- Data freshness: Real-time aggregation
- Scalability: MongoDB aggregation pipelines
- Historical data: 6+ months retention

## Future Enhancements

### Payment Service
- Real payment gateway integration (Stripe/PayPal)
- Automated recurring billing
- Invoice generation
- Refund processing
- Email notifications

### Analytics Service
- More historical data (1-2 years)
- Predictive analytics
- Custom date range selection
- Export to PDF/Excel
- Scheduled report generation
- Comparative analysis (peer benchmarking)

## Troubleshooting

### MongoDB Connection Issues
```bash
# Check MongoDB status
docker ps | grep mongodb
docker logs finova-mongodb

# Verify connection
mongosh mongodb://admin:password@localhost:27017/admin
```

### Service Not Starting
```bash
# Check logs
docker logs payment-service
docker logs analytics-service

# Verify port availability
lsof -i :8084
lsof -i :8085
```

### Frontend Issues
```bash
# Check browser console for errors
# Verify services are running
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health
```

## Related Jira Tickets

- **GAP-133**: OLTP integration with MongoDB (Payment Service)
- **GAP-132**: OLAP integration with MongoDB (Analytics Service)

## Figma Designs

- Payment Screens: https://www.figma.com/design/dekopqGX3soYs2rSBSBSkq/OLTP-payment-screens
- Analytics Dashboard: https://www.figma.com/design/GcDSMfirQMD7MUwHVlPYLM/OLAP

## Contributors

Implementation completed as per specifications in tickets GAP-132 and GAP-133.

