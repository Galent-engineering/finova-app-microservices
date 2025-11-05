# Finova Retirement Planning - Microservices Architecture

## Overview
This project is a comprehensive microservices-based retirement planning application that helps users manage their retirement accounts, contributions, financial planning, payments, and analytics. The system implements both OLTP (Online Transaction Processing) and OLAP (Online Analytical Processing) patterns.

## ✨ Latest Features

### 🆕 Payment Service (OLTP) - Port 8084
Real-time subscription and payment processing with MongoDB integration:
- **3 Subscription Tiers**: Basic ($9.99), Plus ($19.99), Premium ($49.99)
- **Flexible Billing**: Monthly, Quarterly (5% off), Annual (10% off)
- **Payment Processing**: <2 second response time with mock gateway
- **Automatic Retry**: Failed payments automatically retry
- **Full Management**: Pause, cancel, or update subscriptions anytime

### 🆕 Analytics Service (OLAP) - Port 8085
Historical data analysis and insights generation with MongoDB aggregation:
- **6 Months Sample Data**: Auto-generated on first startup
- **Interactive Dashboard**: Key metrics and performance indicators
- **Rich Visualizations**: Line charts, donut charts, bar graphs
- **Quarterly Reports**: Detailed performance breakdown
- **Smart Insights**: AI-driven recommendations based on your data
- **Time-Based Analysis**: View trends over 3m, 6m, 12m, or all time

### 🔧 MongoDB Integration
- **Dual Database Strategy**: PostgreSQL for traditional services, MongoDB for payments & analytics
- **ACID Transactions**: Payment service ensures data consistency
- **Aggregation Pipelines**: Analytics service leverages MongoDB's powerful query capabilities
- **Auto-Indexing**: Optimized for both read and write operations

## 📑 Quick Links
- [Architecture Overview](#current-architecture)
- [Getting Started](#getting-started)
- [Service URLs](#service-urls)
- [Payment Service API](#-payment-service-api-oltp)
- [Analytics Service API](#-analytics-service-api-olap)
- [OLTP vs OLAP Architecture](#️-oltp-vs-olap-architecture)
- [Frontend Features](#frontend-features)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Database Design](#database-design)

## Current Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Frontend (HTML/JS/CSS)                               │
│                         http://localhost:8000                               │
│              ↓ All API requests go through Gateway ↓                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                        API Gateway (9080) ⭐ Enhanced                       │
│    • Service Discovery  • Load Balancing  • CORS  • Rate Limiting           │
│    • Request/Response Headers  • Fallback Responses  • Health Monitoring    │
├─────────────────────────────────────────────────────────────────────────────┤
│  Keycloak    │  User     │  Account   │  Planning  │  Payment   │ Analytics │
│  Auth Server │  Service  │  Service   │  Service   │  Service   │  Service  │
│   (8080)     │  (8081)   │  (8082)    │  (8083)    │  (8084)    │  (8085)   │
│              │           │            │            │  [OLTP]    │  [OLAP]   │
├─────────────────────────────────────────────────────────────────────────────┤
│              Service Discovery - Eureka (8761)                              │
│              Configuration Server (8888)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│         PostgreSQL (User, Account, Planning)  │  MongoDB (Payment, Analytics)│
└─────────────────────────────────────────────────────────────────────────────┘
```

### 🔄 **Request Flow:**
```
Browser → API Gateway (9080) → Service Discovery → Microservices (8081-8085)
   ↑                ↓
   └── Fallback ←───┘ (if service down)
```

## Services

### 1. **Keycloak Auth Server** (Port: 8080) 🔐
- OAuth 2.0 / OpenID Connect authentication
- User management and identity provider
- SSO (Single Sign-On) capabilities
- Role-based access control

### 2. **API Gateway** (Port: 9080) ⭐ **Enhanced**
- **Single entry point** for all client requests
- **Dynamic service discovery** via Eureka
- **Load balancing** with automatic failover
- **CORS handling** for browser compatibility
- **Rate limiting** (Redis-based, optional)
- **Request/response headers** for tracking
- **Fallback responses** when services unavailable
- **Health monitoring** and route inspection

### 3. **Eureka Server** (Port: 8761)
- Service discovery and registration
- Monitors health of all microservices
- Auto-discovery for dynamic scaling

### 4. **Config Server** (Port: 8888)
- Centralized configuration management
- Environment-specific configurations
- Hot reload of configurations

### 5. **User Service** (Port: 8081)
- User authentication and authorization
- User profile management
- JWT token generation and validation
- Keycloak integration

### 6. **Account Service** (Port: 8082)
- Retirement account management
- Contribution tracking
- Income source management
- Financial data aggregation

### 7. **Planning Service** (Port: 8083)
- Retirement calculators
- Financial planning tools
- Investment strategy recommendations
- Projection algorithms

### 8. **Payment Service** (Port: 8084) 💳 **[OLTP]**
- **Real-time transaction processing** (<2 seconds)
- **Advisory subscription management** (Basic, Plus, Premium tiers)
- **Billing frequency options** (Monthly, Quarterly, Annual with discounts)
- **Payment method management** and updates
- **Mock payment gateway** (90% success rate simulation)
- **Automatic retry mechanism** for failed payments
- **Subscription pause/cancel** functionality
- **Payment history tracking** and reporting
- **MongoDB integration** for transactional data

### 9. **Analytics Service** (Port: 8085) 📊 **[OLAP]**
- **Historical data analysis** (6 months of auto-generated sample data)
- **Real-time dashboard** with key metrics
- **Interactive charts** (Line, Donut, Bar graphs)
- **Quarterly performance analysis** and trends
- **Contribution breakdown** analysis
- **Account distribution** insights
- **Actionable recommendations** based on data
- **MongoDB aggregation pipelines** for complex queries
- **Time-based reporting** (3m, 6m, 12m, all time)

### 10. **Frontend Application** (Port: 8000)
- **Modern, responsive web interface**
- **Routes through API Gateway (9080)**
- **Interactive retirement planning dashboard**
- **Real-time service monitoring** (including gateway)
- **Account and contribution management**
- **Planning tools and calculators**
- **Payment and subscription management**
- **Analytics dashboard with visualizations**
- **CORS-enabled** for gateway integration

## Technology Stack

**Backend:**
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Cloud 2022.0.4**
- **PostgreSQL** (User, Account, Planning services)
- **MongoDB 7.0** (Payment, Analytics services)
- **Docker & Docker Compose**
- **Maven** (Build tool)

**Frontend:**
- **HTML5, CSS3, JavaScript (ES6+)**
- **Font Awesome 6.0** (Icons)
- **Responsive Design** (Mobile-first)
- **REST API Integration** (Fetch API)
- **Single Page Application** (SPA)

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL (if not using Docker)
- MongoDB (if not using Docker - required for Payment & Analytics services)

### Running the Application

**Option 1: Quick Start (Recommended for Development)**

1. **Start databases**
```bash
# Start PostgreSQL and MongoDB
docker-compose -f docker-compose-simple.yml up -d

# Verify databases are running
docker ps
```

2. **Run the automated startup script**
```bash
# Windows
start-services.bat

# Manual startup (if script doesn't work)
mvn clean compile
# Then start services in separate terminals (in order):
cd config-server && mvn spring-boot:run
cd eureka-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd account-service && mvn spring-boot:run
cd planning-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd analytics-service && mvn spring-boot:run
```

3. **Start the Frontend**
```bash
# Navigate to frontend directory
cd frontend

# Option A: Use the provided startup script (Windows)
start-frontend.bat

# Option B: Start manually with Python
python -m http.server 8000

# Option C: Or use Node.js
npx http-server -p 8000

# Option D: Or use PHP
php -S localhost:8000
```

**Option 2: Full Docker (Advanced)**

*Note: Docker build requires additional setup. Use Option 1 for initial testing.*

1. **Build and run all services**
```bash
# First build the project
mvn clean package -DskipTests

# Then run with Docker (currently needs fixes)
docker-compose up -d
```

### Service URLs
- **🌐 Frontend Application**: http://localhost:8000
- **⭐ API Gateway (Enhanced)**: http://localhost:9080
  - Health: http://localhost:9080/actuator/health
  - Routes: http://localhost:9080/actuator/gateway/routes
  - Fallbacks: http://localhost:9080/fallback/
- **🔐 Keycloak Auth Server**: http://localhost:8080
- **📡 Eureka Dashboard**: http://localhost:8761
- **⚙️ Config Server**: http://localhost:8888
- **👤 User Service**: http://localhost:8081
- **💰 Account Service**: http://localhost:8082  
- **📊 Planning Service**: http://localhost:8083
- **💳 Payment Service (OLTP)**: http://localhost:8084
- **📈 Analytics Service (OLAP)**: http://localhost:8085

## 🚀 API Gateway Features (NEW)

### ✅ **Production-Ready Capabilities**
- **Service Discovery Integration**: Auto-discovers services via Eureka
- **Load Balancing**: `lb://service-name` protocol for automatic load distribution
- **CORS Support**: Configured for `http://localhost:8000` (frontend)
- **Health Monitoring**: `/actuator/health` and `/actuator/gateway/routes`
- **Request/Response Headers**: Custom headers for tracking and debugging
- **Fallback Responses**: Graceful degradation when services are unavailable

### 🔧 **Development Features**  
- **Route Inspection**: View all active routes at `/actuator/gateway/routes`
- **Service-Specific Fallbacks**: Different responses per service type
- **Enhanced CORS**: Supports development and production origins
- **Request Tracking**: `X-Request-Source` and `X-Gateway-Response` headers

### 🌐 **Frontend Integration**
The frontend now routes **all API requests** through the gateway:
```javascript
// Before: Direct service calls
fetch('http://localhost:8082/api/accounts/user/1')

// After: Through API Gateway  
fetch('http://localhost:9080/api/accounts/user/1')

// New services accessible through gateway:
// Payment Service
fetch('http://localhost:9080/api/subscriptions/1/active')
fetch('http://localhost:9080/api/payments/1')

// Analytics Service
fetch('http://localhost:9080/api/analytics/dashboard/1?period=6m')
```

### 📊 **Monitoring & Testing**
- **Service Tester**: http://localhost:8000/test-services.html
- **Gateway Health**: http://localhost:9080/actuator/health  
- **Active Routes**: http://localhost:9080/actuator/gateway/routes
- **Fallback Testing**: http://localhost:9080/fallback/user-service
- **Payment Service Health**: http://localhost:8084/actuator/health
- **Analytics Service Health**: http://localhost:8085/actuator/health

## 💳 Payment Service API (OLTP)

### Key Endpoints:
```bash
# Subscription Management
GET    /api/subscriptions/{userId}                    # Get all subscriptions
GET    /api/subscriptions/{userId}/active             # Get active subscription
POST   /api/subscriptions                             # Create subscription
PUT    /api/subscriptions/{subscriptionId}/pause      # Pause subscription
PUT    /api/subscriptions/{subscriptionId}/cancel     # Cancel subscription

# Payment Processing
POST   /api/payments/process                          # Process payment
GET    /api/payments/{userId}                         # Get payment history
GET    /api/payments/{userId}/recent?limit=10         # Get recent payments

# Payment Methods
POST   /api/payment-methods                           # Add payment method
GET    /api/payment-methods/{userId}                  # Get user payment methods
PUT    /api/payment-methods/{methodId}                # Update payment method
```

### Subscription Plans:
- **Basic**: $9.99/month - Essential retirement tools
- **Plus**: $19.99/month - Advanced planning features  
- **Premium**: $49.99/month - Full advisory service

### Billing Frequencies:
- **Monthly**: Full price
- **Quarterly**: 5% discount
- **Annual**: 10% discount

## 📊 Analytics Service API (OLAP)

### Key Endpoints:
```bash
# Dashboard & Overview
GET    /api/analytics/dashboard/{userId}?period=6m    # Dashboard with key metrics
GET    /api/analytics/overview/{userId}               # Quick overview stats

# Historical Data
GET    /api/analytics/balance-history/{userId}?period=6m   # Balance trends
GET    /api/analytics/contributions/{userId}?period=6m     # Contribution history
GET    /api/analytics/quarterly/{userId}                   # Quarterly performance

# Insights & Recommendations
GET    /api/analytics/insights/{userId}               # AI-driven insights
GET    /api/analytics/account-breakdown/{userId}      # Account distribution
```

### Analytics Features:
- **Auto-Generated Sample Data**: 6 months of historical data on first startup
- **Time Periods**: 3 months, 6 months, 12 months, all time
- **Aggregation Pipelines**: MongoDB-powered analytics
- **Real-time Calculations**: Dynamic metrics and growth rates
- **Visual Data**: Ready for charts and graphs

## 🏗️ Architecture Classification: Microservices (Not a Monolith)

### ✅ **This is a Microservices Architecture**

**Key Indicators that confirm this is NOT a monolith:**

1. **Independent Services**
   - ✅ **10 separate services** running on different ports (8080-8085, 8761, 8888, 9080)
   - ✅ Each service can be **started/stopped independently**
   - ✅ Each service has its **own codebase** (separate Maven modules)
   - ✅ Services can be **deployed independently** without affecting others

2. **Service Discovery & Registration**
   - ✅ **Eureka Server** (8761) for dynamic service discovery
   - ✅ Services register themselves and discover each other
   - ✅ Enables **horizontal scaling** (multiple instances of same service)

3. **API Gateway Pattern**
   - ✅ **Spring Cloud Gateway** (9080) as single entry point
   - ✅ Routes requests to appropriate microservices
   - ✅ Load balancing across service instances
   - ✅ Centralized cross-cutting concerns (CORS, rate limiting, authentication)

4. **Database per Service Pattern**
   - ✅ **PostgreSQL**: User Service, Account Service, Planning Service (separate databases)
   - ✅ **MongoDB**: Payment Service, Analytics Service (separate databases)
   - ✅ Each service owns its data - **no shared database** (except infrastructure services)

5. **Decentralized Configuration**
   - ✅ **Config Server** (8888) for centralized configuration management
   - ✅ Services fetch configuration independently
   - ✅ Environment-specific configurations

6. **Distributed Communication**
   - ✅ Services communicate via **HTTP/REST APIs**
   - ✅ Services communicate through **API Gateway** (not direct calls)
   - ✅ **No shared memory** or in-process communication

7. **Independent Technology Stacks**
   - ✅ **PostgreSQL** for relational services
   - ✅ **MongoDB** for document-based services
   - ✅ Different data models optimized per service

### 🔍 **Potential "Monolith-like" Characteristics (Not Actually Monolithic)**

These might seem monolithic, but they're actually **common patterns** in microservices:

1. **Parent POM (pom.xml)**
   - ✅ **Shared dependency management** - This is a **best practice**, not monolithic
   - ✅ Ensures consistent versions across services
   - ✅ Does NOT mean services are bundled together
   - ✅ Each service still builds and runs independently

2. **Monorepo Structure**
   - ✅ All services in one repository
   - ✅ This is **code organization**, not architecture
   - ✅ Services can still be deployed independently
   - ✅ Many large companies use monorepos with microservices

3. **Shared Infrastructure Services**
   - ✅ Eureka, Config Server, API Gateway are shared
   - ✅ These are **infrastructure services**, not business logic
   - ✅ Standard pattern in microservices architecture
   - ✅ Similar to how Kubernetes, Docker are shared infrastructure

### 📊 **Comparison: Monolith vs This Architecture**

| Characteristic | Monolith | This Architecture |
|----------------|----------|-------------------|
| **Deployment** | Single deployable unit | 10+ independent services |
| **Scaling** | Scale entire application | Scale individual services |
| **Database** | Single shared database | Database per service (5+ databases) |
| **Technology** | Fixed tech stack | Mix of PostgreSQL + MongoDB |
| **Service Discovery** | Not needed (in-process calls) | Eureka Service Registry |
| **API Gateway** | Not needed (direct access) | Spring Cloud Gateway |
| **Configuration** | In-process config | Centralized Config Server |
| **Fault Isolation** | One failure affects all | Isolated service failures |
| **Team Ownership** | Shared codebase | Service ownership |

### 🎯 **Conclusion**

**This architecture is definitively a microservices architecture**, not a monolith. The presence of:
- Multiple independent services
- Service discovery (Eureka)
- API Gateway
- Database per service pattern
- Distributed communication

...are all **hallmarks of microservices architecture**.

The shared parent POM and monorepo structure are **organizational patterns** that don't affect the architectural style. Each service is independently deployable, scalable, and maintainable.

---

## 🏗️ OLTP vs OLAP Architecture

This application demonstrates both **OLTP** (Online Transaction Processing) and **OLAP** (Online Analytical Processing) patterns:

### OLTP - Payment Service (Port 8084)
**Purpose**: Handle real-time transactions with immediate consistency

**Characteristics**:
- ✅ **Low latency**: Payment processing under 2 seconds
- ✅ **High concurrency**: Handle multiple simultaneous payments
- ✅ **ACID transactions**: Ensure data consistency
- ✅ **Normalized data**: Efficient updates and inserts
- ✅ **Real-time validation**: Immediate payment confirmation

**Use Cases**:
- Processing subscription payments
- Updating payment methods
- Managing subscription status (pause/cancel)
- Recording transaction history

### OLAP - Analytics Service (Port 8085)
**Purpose**: Analyze historical data and generate insights

**Characteristics**:
- ✅ **Complex queries**: Aggregation pipelines for deep analysis
- ✅ **Historical data**: 6 months of time-series data
- ✅ **Read-optimized**: Denormalized for fast queries
- ✅ **Batch processing**: Generate quarterly reports
- ✅ **Trend analysis**: Calculate growth rates and patterns

**Use Cases**:
- Generating retirement readiness scores
- Analyzing contribution patterns
- Visualizing account balance trends
- Creating quarterly performance reports
- Providing actionable financial insights

### Why MongoDB for Both?
- **OLTP (Payment)**: MongoDB's ACID transactions and low latency make it ideal for payment processing
- **OLAP (Analytics)**: MongoDB's aggregation framework excels at complex analytical queries
- **Flexibility**: Schema-less design accommodates evolving data requirements
- **Scalability**: Both services can scale independently based on load

## Frontend Features

### 🏠 Dashboard
- **Financial Overview**: Monthly income projections and current balance
- **Retirement Readiness**: Assessment of retirement planning progress
- **Quick Actions**: Easy navigation to main features
- **Real-time Service Monitoring**: Health status of all microservices

### 📊 Account Management
- **Overview Tab**: Summary of all retirement accounts and contributions
- **Manage Accounts**: Add, edit, and view retirement accounts (401k, IRA, etc.)
- **Manage Contributions**: Configure contribution settings and amounts
- **Income Sources**: Track multiple retirement income streams

### 🧮 Planning Tools
- **Retirement Calculator**: Interactive compound interest calculator
- **What-If Scenarios**: Compare different planning strategies
- **Social Security Calculator**: Estimate benefits at different ages
- **Investment Strategy**: View personalized investment recommendations

### 💳 Payment Management (OLTP)
- **Subscription Management**: Select and manage advisory service subscriptions
  - **Basic Plan**: $9.99/month - Essential retirement planning tools
  - **Plus Plan**: $19.99/month - Advanced planning + professional tips
  - **Premium Plan**: $49.99/month - Full-service financial advisory
- **Billing Frequency**: Choose Monthly, Quarterly (5% off), or Annual (10% off)
- **Payment Methods**: Add, update, and manage payment methods
- **Payment History**: View all past transactions and invoices
- **Subscription Control**: Pause or cancel subscriptions anytime
- **Real-time Processing**: Instant payment confirmation (<2 seconds)
- **Automatic Retry**: Failed payments automatically retry with notifications

### 📊 Analytics Dashboard (OLAP)
- **Key Performance Metrics**:
  - Total Balance across all accounts
  - Total Contributions (Employee + Employer)
  - Average Monthly Growth rate
  - Retirement Readiness Score
- **Interactive Visualizations**:
  - **Balance Trends**: Line chart showing 6-month balance history
  - **Account Distribution**: Donut chart breaking down accounts by type
  - **Contribution Analysis**: Bar chart comparing employee vs employer contributions
- **Time Period Selection**: View data for 3 months, 6 months, 12 months, or all time
- **Quarterly Performance**: Detailed quarterly breakdown with growth metrics
- **Actionable Insights**: AI-driven recommendations based on your data
- **Account Breakdown**: See individual account performance and contributions

### ⚙️ System Monitoring
- **Microservices Status**: Real-time health checks for all services (including Payment & Analytics)
- **API Endpoints**: Documentation of available endpoints
- **Architecture Overview**: Visual representation of system components
- **Service Connection Testing**: Built-in debugging tools

### 📱 Technical Features
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile
- **Progressive Enhancement**: Graceful fallbacks when services are unavailable
- **Error Handling**: User-friendly error messages and recovery options
- **API Gateway Integration**: All requests routed through enhanced gateway (port 9080)
- **Intelligent Fallback**: Gateway provides fallback responses when services are down
- **Service Discovery**: Dynamic service routing via Eureka integration
- **Cross-Origin Support**: CORS-enabled for development and production

### 🔧 Development Tools
- **Service Connection Tester**: `test-services.html` for debugging connectivity
- **Comprehensive Documentation**: Detailed setup and usage instructions
- **Browser Console Integration**: Debug functions and logging
- **Modular Architecture**: Easy to extend and customize

## Development

### Adding New Services
1. Create new module in parent POM
2. Add service discovery client dependency
3. Configure service registration in application.yml
4. Implement business logic

### Configuration Management
All configurations are managed centrally via the Config Server. Service-specific configurations are stored in the `config-repo` directory.

## Project Structure

```
finova-retire-app-microsrv/
├── frontend/                    # Frontend application
│   ├── index.html              # Main application page
│   ├── payments.html           # Payment & subscription management
│   ├── analytics.html          # Analytics dashboard
│   ├── style.css               # Complete styling system
│   ├── script.js               # Application logic
│   ├── test-services.html      # Service connectivity tester
│   ├── start-frontend.bat      # Windows startup script
│   └── README.md               # Frontend documentation
├── api-gateway/                # API Gateway service (Port 9080)
├── user-service/               # User management service (Port 8081)
├── account-service/            # Account management service (Port 8082)
├── planning-service/           # Planning tools service (Port 8083)
├── payment-service/            # Payment service - OLTP (Port 8084, MongoDB)
├── analytics-service/          # Analytics service - OLAP (Port 8085, MongoDB)
├── eureka-server/              # Service discovery (Port 8761)
├── config-server/              # Configuration management (Port 8888)
├── docker-compose.yml         # Full Docker setup
├── docker-compose-simple.yml  # Database-only Docker (PostgreSQL + MongoDB)
├── start-services.bat          # Windows startup script
├── pom.xml                     # Parent Maven configuration
├── README.md                   # This file
├── QUICK_START.md              # Quick start guide
└── OLTP_OLAP_INTEGRATION_README.md  # Payment & Analytics integration guide
```

## Testing

**Backend Testing:**
```bash
# Run all tests
mvn test

# Run specific service tests
cd user-service && mvn test
cd payment-service && mvn test
cd analytics-service && mvn test
```

**Payment Service Testing:**
```bash
# Test subscription creation
curl -X POST http://localhost:8084/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "planType": "PLUS",
    "frequency": "MONTHLY"
  }'

# Get active subscription
curl http://localhost:8084/api/subscriptions/1/active

# Process payment
curl -X POST http://localhost:8084/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "subscriptionId": 1,
    "userId": 1,
    "paymentMethodId": "pm_123"
  }'

# Get payment history
curl http://localhost:8084/api/payments/1
```

**Analytics Service Testing:**
```bash
# Get dashboard data
curl "http://localhost:8085/api/analytics/dashboard/1?period=6m"

# Get balance history
curl "http://localhost:8085/api/analytics/balance-history/1?period=6m"

# Get quarterly performance
curl http://localhost:8085/api/analytics/quarterly/1

# Get insights
curl http://localhost:8085/api/analytics/insights/1
```

**Frontend Testing:**
```bash
# Manual testing checklist
1. Open http://localhost:8000
2. Verify all service status cards show correct status
3. Test navigation between sections
4. Test account management forms
5. Test planning calculators
6. Test payment subscription flow (http://localhost:8000/payments.html)
7. Test analytics dashboard (http://localhost:8000/analytics.html)
8. Verify charts and visualizations load correctly
9. Verify responsive design on different devices

# Use service connection tester
Open http://localhost:8000/test-services.html
```

## Monitoring
- **Health Checks**: Available at `/actuator/health` for each service
- **Metrics**: Prometheus metrics at `/actuator/prometheus`
- **Service Discovery**: Monitor via Eureka dashboard

## Security
- **JWT-based authentication** via Keycloak
- **Service-to-service communication security**
- **API Gateway** handles authentication/authorization
- **Payment security**: PCI-compliant payment processing simulation
- **Data encryption**: Sensitive payment data encrypted at rest
- **CORS protection**: Configured for trusted origins only

## Troubleshooting

### Frontend Issues

**Services showing offline:**
1. Verify all microservices are running on correct ports
2. Check browser console for CORS errors
3. Use service connection tester: `http://localhost:8000/test-services.html`
4. Try accessing services directly: `http://localhost:8083/api/planning/health`

**Frontend not loading:**
1. Ensure web server is running on port 8000
2. Check for JavaScript errors in browser console
3. Verify all files are in the frontend directory
4. Try different browsers (Chrome, Firefox, Edge)

**Calculator not working:**
1. Check JavaScript console for errors
2. Verify Planning Service is responding
3. Test with fallback data (should work even if services are down)

### Backend Issues

**Services not starting:**
1. Ensure Java 17 is installed
2. Check if ports are available (8080, 8081, 8082, 8083, 8761, 8888)
3. Verify Maven dependencies are resolved
4. Check application logs for specific errors

**Service discovery issues:**
1. Start Eureka Server first
2. Wait for services to register (may take 30-60 seconds)
3. Check Eureka dashboard: `http://localhost:8761`

**MongoDB connection issues:**
1. Ensure MongoDB is running: `docker ps | grep mongodb`
2. Check connection string in application.yml files
3. Verify credentials (default: admin/password)
4. Check MongoDB logs: `docker logs finova-mongodb`

**Payment Service issues:**
1. Verify MongoDB is accessible at port 27017
2. Check service logs for payment processing errors
3. Test mock payment gateway (90% success rate by design)
4. Retry failed payments using the automatic retry mechanism

**Analytics Service issues:**
1. Wait for sample data generation on first startup (~30 seconds)
2. Check MongoDB collections: account_snapshots, contributions, quarterly_stats
3. Verify aggregation pipeline queries in logs
4. Ensure time period parameter is valid (3m, 6m, 12m, all)

## Database Design
Each service has its own database following the database-per-service pattern:

### PostgreSQL Databases (Relational - OLTP):
- **user_service_db**: User profiles and authentication
- **account_service_db**: Retirement accounts and contributions
- **planning_service_db**: Financial calculations and projections

### MongoDB Databases (Document - OLTP/OLAP):
- **payment_db**: Subscription plans, payment transactions, and payment methods
  - Collections: `subscriptions`, `payments`, `payment_methods`
  - Optimized for: Real-time transaction processing
  - Features: ACID transactions, payment history tracking
- **analytics_db**: Historical account data and performance metrics
  - Collections: `account_snapshots`, `contributions`, `quarterly_stats`
  - Optimized for: Aggregation pipelines, time-series analysis
  - Features: 6 months historical data, trend analysis, reporting

## Contributing

### Frontend Development
1. Follow existing code style and conventions
2. Test changes across different browsers
3. Update documentation for new features
4. Ensure responsive design principles are maintained
5. Test with services both online and offline

### Backend Development
1. Follow Spring Boot best practices
2. Implement proper error handling
3. Add comprehensive tests
4. Update API documentation
5. Ensure service discovery compatibility

## 📋 Complete Services Summary

| Service | Port | Type | Database | Purpose |
|---------|------|------|----------|---------|
| **Frontend** | 8000 | Web UI | - | User interface and visualization |
| **Keycloak Auth** | 8080 | Identity | - | OAuth 2.0 / OpenID Connect authentication |
| **API Gateway** | 9080 | Gateway | - | Single entry point, routing, load balancing |
| **Eureka Server** | 8761 | Discovery | - | Service registration and discovery |
| **Config Server** | 8888 | Config | - | Centralized configuration management |
| **User Service** | 8081 | Business | PostgreSQL | User profiles and authentication |
| **Account Service** | 8082 | Business | PostgreSQL | Retirement account management |
| **Planning Service** | 8083 | Business | PostgreSQL | Financial planning and calculators |
| **Payment Service** | 8084 | OLTP | MongoDB | Real-time payment processing |
| **Analytics Service** | 8085 | OLAP | MongoDB | Historical data analysis and insights |

### Service Dependencies
```
Frontend (8000)
    ↓
API Gateway (9080) ← relies on → Eureka (8761) ← relies on → Config Server (8888)
    ↓
┌───┴───┬────────┬─────────┬─────────┬──────────┐
│       │        │         │         │          │
User   Account  Planning Payment  Analytics  Keycloak
8081   8082     8083     8084     8085       8080
```

### Technology Matrix

| Technology | Services |
|------------|----------|
| **Java 17 + Spring Boot** | All backend services |
| **PostgreSQL** | User, Account, Planning |
| **MongoDB 7.0** | Payment, Analytics |
| **Spring Cloud** | Gateway, Eureka, Config |
| **Keycloak** | Authentication Server |
| **HTML/CSS/JS** | Frontend Application |
| **Docker** | All services (optional) |

## 🎯 Key Highlights

✅ **10 Microservices** working in harmony  
✅ **2 Database Types**: PostgreSQL (relational) + MongoDB (document)  
✅ **OLTP & OLAP**: Real-time processing + historical analytics  
✅ **Cloud-Native**: Service discovery, config management, API gateway  
✅ **Modern Frontend**: Responsive SPA with interactive visualizations  
✅ **Production-Ready**: Health checks, monitoring, error handling  
✅ **Developer-Friendly**: Hot reload, centralized config, easy testing  

## 📚 Additional Documentation
- **[QUICK_START.md](QUICK_START.md)**: Step-by-step startup guide
- **[OLTP_OLAP_INTEGRATION_README.md](OLTP_OLAP_INTEGRATION_README.md)**: Detailed Payment & Analytics integration guide
- **Frontend Documentation**: See `frontend/README.md` for UI details

## 📞 Support & Issues
For issues, questions, or contributions, please refer to the troubleshooting section or check the individual service documentation.

---

**Built with ❤️ using Spring Boot, MongoDB, PostgreSQL, and modern microservices architecture**
