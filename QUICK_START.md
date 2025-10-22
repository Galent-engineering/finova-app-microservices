# Finova Microservices - Complete Quick Start Guide


## Prerequisites

- **Java 17** (Not 22 or 25!)
- **Maven 3.9+**
- **Docker & Docker Compose**
- **Python 3** (for frontend server)

### Ports Required
Make sure these ports are available:
- 8000 (Frontend)
- 8080 (API Gateway)
- 8081-8085 (Microservices)
- 8761 (Eureka)
- 8888 (Config Server)
- 9080 (Keycloak)
- 27017 (MongoDB)

---

## Step 1: Set Up Java 17 (CRITICAL!)

**Check your Java version first:**
```bash
java -version
```

### If you see Java 25, 22, or anything other than 17:

```bash
# Check available Java versions
/usr/libexec/java_home -V

# If Java 17 is installed, set it:
export JAVA_HOME=`/usr/libexec/java_home -v 17`

# If Java 17 is NOT installed:
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# Verify (should show 17.x.x)
java -version
```

**Make it permanent** (add to `~/.zshrc` or `~/.bash_profile`):
```bash
export JAVA_HOME=`/usr/libexec/java_home -v 17`
```

---

## Step 2: Start Infrastructure (Docker)

Navigate to project root:
```bash
cd /Users/architgupta/POCs/finova-app-microservices
```

### Start Keycloak (SSO Provider)
```bash
docker-compose -f docker-compose-keycloak.yml up -d
```

**⏱️ Wait 15 seconds** for Keycloak to start and import the realm.

**Verify Keycloak:**
```bash
curl http://localhost:9080/realms/finova/.well-known/openid-configuration
# Should return JSON with OAuth config
```

### Start MongoDB (Analytics & Payments Database)
```bash
docker-compose up -d mongodb
```

**Verify MongoDB:**
```bash
docker exec finova-mongodb mongosh -u admin -p password --authenticationDatabase admin --eval "db.adminCommand('ping')"
# Should return: { ok: 1 }
```

---

## Step 3: Build All Services

```bash
cd /Users/architgupta/POCs/finova-app-microservices
mvn clean install -DskipTests
```

**Expected:** All 7 services should build successfully.

---

## Step 4: Start Microservices (In Order)

Open **8 separate terminal windows/tabs** and run these commands **in order**:

### Terminal 1 - Config Server (Configuration Management)
```bash
cd /Users/architgupta/POCs/finova-app-microservices/config-server
mvn spring-boot:run
```
**✅ Wait for:** `Started ConfigServerApplication`  
**🌐 Port:** 8888  
**Note:** Config Server should start first to provide centralized configuration.

---

### Terminal 2 - Eureka Server (Service Discovery)
```bash
cd /Users/architgupta/POCs/finova-app-microservices/eureka-server
mvn spring-boot:run
```
**✅ Wait for:** `Started EurekaServerApplication`  
**🌐 URL:** http://localhost:8761

---

### Terminal 3 - API Gateway
```bash
cd /Users/architgupta/POCs/finova-app-microservices/api-gateway
mvn spring-boot:run
```
**✅ Wait for:** `Started ApiGatewayApplication`  
**🌐 Port:** 8080

---

### Terminal 4 - User Service
```bash
cd /Users/architgupta/POCs/finova-app-microservices/user-service
mvn spring-boot:run
```
**✅ Wait for:** `Started UserServiceApplication`  
**🌐 Port:** 8081

---

### Terminal 5 - Account Service
```bash
cd /Users/architgupta/POCs/finova-app-microservices/account-service
mvn spring-boot:run
```
**✅ Wait for:** `Started AccountServiceApplication`  
**🌐 Port:** 8082

---

### Terminal 6 - Planning Service
```bash
cd /Users/architgupta/POCs/finova-app-microservices/planning-service
mvn spring-boot:run
```
**✅ Wait for:** `Started PlanningServiceApplication`  
**🌐 Port:** 8083

---

### Terminal 7 - Payment Service (OLTP)
```bash
cd /Users/architgupta/POCs/finova-app-microservices/payment-service
mvn spring-boot:run
```
**✅ Wait for:** `Started PaymentServiceApplication`  
**🌐 Port:** 8084

---

### Terminal 8 - Analytics Service (OLAP)
```bash
cd /Users/architgupta/POCs/finova-app-microservices/analytics-service
mvn spring-boot:run
```
**✅ Wait for:** `Started AnalyticsServiceApplication` + `Sample data generation complete!`  
**🌐 Port:** 8085  
**Note:** This service auto-generates 6 months of sample data on first startup.

---

## Step 5: Start Frontend

### Terminal 9 - Web UI
```bash
cd /Users/architgupta/POCs/finova-app-microservices/frontend
python3 -m http.server 8000
```

**🌐 Open Browser:** http://localhost:8000

---

## Step 6: Verify Everything is Running

Run this quick health check:

```bash
#!/bin/bash
echo "🔍 Checking all services..."
echo ""

services=(
  "Keycloak:http://localhost:9080/health"
  "Config Server:http://localhost:8888/actuator/health"
  "Eureka:http://localhost:8761/actuator/health"
  "API Gateway:http://localhost:8080/actuator/health"
  "User Service:http://localhost:8081/actuator/health"
  "Account Service:http://localhost:8082/actuator/health"
  "Planning Service:http://localhost:8083/actuator/health"
  "Payment Service:http://localhost:8084/actuator/health"
  "Analytics Service:http://localhost:8085/actuator/health"
)

for service in "${services[@]}"; do
  name="${service%%:*}"
  url="${service##*:}"
  status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
  
  if [ "$status" = "200" ]; then
    echo "✅ $name: UP"
  else
    echo "❌ $name: DOWN (HTTP $status)"
  fi
done
```

Save as `check-services.sh`, then run:
```bash
chmod +x check-services.sh
./check-services.sh
```

---

## Step 7: Test the Application

### 🔐 Test SSO Authentication

**Get an access token:**
```bash
curl -X POST 'http://localhost:9080/realms/finova/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=finova-frontend' \
  -d 'username=john.doe' \
  -d 'password=password123' \
  -d 'grant_type=password' | jq
```

**Extract the `access_token` and test protected endpoints:**
```bash
export TOKEN="YOUR_ACCESS_TOKEN_HERE"

# Test user service
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/users/me

# Test account service
curl -H "Authorization: Bearer $TOKEN" http://localhost:8082/api/accounts/user/1

# Test planning service
curl -H "Authorization: Bearer $TOKEN" http://localhost:8083/api/planning/health
```

### 💳 Test Payment Service (OLTP)

**Create a subscription:**
```bash
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
```

**Process a payment:**
```bash
curl -X POST http://localhost:8084/api/payments/process \
  -H "Content-Type: application/json" \
  -d '{
    "subscriptionId": "YOUR_SUBSCRIPTION_ID",
    "userId": 1,
    "amount": 500.00,
    "paymentMethod": {
      "type": "CREDIT_CARD",
      "last4": "1234",
      "brand": "Visa"
    }
  }'
```

### 📊 Test Analytics Service (OLAP)

**Get dashboard data:**
```bash
# 12 months of data
curl "http://localhost:8085/api/analytics/dashboard/1?period=12m" | jq

# 6 months of data
curl "http://localhost:8085/api/analytics/dashboard/1?period=6m" | jq

# 3 months of data
curl "http://localhost:8085/api/analytics/dashboard/1?period=3m" | jq
```

### 🖥️ Test Frontend

1. Open http://localhost:8000
2. Click **"Login with SSO"**
3. Use credentials: `john.doe` / `password123`
4. After login, explore:
   - **Dashboard** - Overview of finances
   - **Manage Billing** - Payment subscriptions
   - **Analytics** - Charts and insights

---

## Pre-configured Test Users

| Username | Password | Roles | Use Case |
|----------|----------|-------|----------|
| john.doe | password123 | USER | Regular user testing |
| jane.admin | admin123 | ADMIN, USER | Admin operations |
| advisor.mike | advisor123 | FINANCIAL_ADVISOR, USER | Financial advisor features |

---

## Service Overview

| Service | Port | Purpose | Tech Stack |
|---------|------|---------|------------|
| **Keycloak** | 9080 | SSO/OAuth 2.0 | Identity Provider |
| **MongoDB** | 27017 | NoSQL Database | Analytics & Payments |
| **Config Server** | 8888 | Configuration Management | Spring Cloud Config |
| **Eureka** | 8761 | Service Discovery | Spring Cloud Netflix |
| **API Gateway** | 8080 | API Routing | Spring Cloud Gateway |
| **User Service** | 8081 | User Management | Spring Boot + OAuth |
| **Account Service** | 8082 | Account Management | Spring Boot + OAuth |
| **Planning Service** | 8083 | Financial Planning | Spring Boot + OAuth |
| **Payment Service** | 8084 | Payment Processing (OLTP) | Spring Boot + MongoDB |
| **Analytics Service** | 8085 | Analytics & Reporting (OLAP) | Spring Boot + MongoDB |
| **Frontend** | 8000 | Web UI | HTML/JS + Chart.js |

---

## Troubleshooting

### ❌ Build fails with Java error
**Symptom:** `ExceptionInInitializerError: TypeTag :: UNKNOWN`  
**Solution:** You're not using Java 17. See Step 1 above.

### ❌ Service can't connect to Keycloak
**Symptom:** `Unable to resolve Configuration with provided Issuer`  
**Solution:**
```bash
# Check Keycloak is running
docker ps | grep keycloak
curl http://localhost:9080/realms/finova

# Wait 15 seconds after starting Keycloak
# Then restart the service
```

### ❌ MongoDB authentication failed
**Symptom:** `MongoSecurityException: Authentication failed`  
**Solution:**
```bash
# Recreate MongoDB with fresh credentials
docker-compose down mongodb
docker volume rm finova-app-microservices_mongodb_data
docker-compose up -d mongodb
# Wait 10 seconds, then restart services
```

### ❌ Port already in use
**Solution:**
```bash
# Find what's using the port
lsof -i :8080  # Replace 8080 with your port

# Kill the process
kill -9 <PID>
```

### ❌ No data in Analytics dashboard
**Solution:**
```bash
# Check Analytics service logs - should see "Sample data generation complete!"
# If not, restart Analytics service
cd analytics-service
mvn spring-boot:run
```

### ❌ Frontend shows CORS errors
**Solution:** Use the Python HTTP server (Step 5), not `file://` URLs directly.

---

## Stopping Services

### Stop Spring Boot Services
Press `Ctrl+C` in each terminal running a service.

### Stop Docker Containers
```bash
# Stop Keycloak
docker-compose -f docker-compose-keycloak.yml down

# Stop MongoDB
docker-compose down mongodb

# To remove all data (use with caution):
docker-compose down -v
```

---

## Quick Demo Flow

Perfect for showcasing all features:

1. **Start all services** (Steps 1-5)
2. **Open frontend** → http://localhost:8000
3. **Login with SSO** → Use john.doe / password123
4. **Dashboard** → View financial overview
5. **Analytics** → Switch between 3M/6M/12M periods
6. **Manage Billing** → Create a Premium subscription
7. **Change frequency** → Switch to Annual (see 10% discount)
8. **View Charts** → Savings growth, contributions, returns
9. **Check Insights** → AI-powered recommendations

---

## Success Checklist

Before considering setup complete, verify:

- ✅ Java 17 is active (`java -version`)
- ✅ All 8 Maven services built successfully
- ✅ Keycloak accessible at http://localhost:9080
- ✅ MongoDB accepting connections
- ✅ All 9 health endpoints return 200 OK
- ✅ Can get OAuth token from Keycloak
- ✅ Protected endpoints require Bearer token
- ✅ Frontend loads at http://localhost:8000
- ✅ SSO login redirects to Keycloak
- ✅ Analytics dashboard shows charts
- ✅ Payment page shows subscription management

---

## Additional Resources

- **Keycloak Admin Console:** http://localhost:9080 (admin/admin123)
- **Eureka Dashboard:** http://localhost:8761
- **MongoDB Shell:**
  ```bash
  docker exec -it finova-mongodb mongosh -u admin -p password
  use payment_db
  db.subscriptions.find().pretty()
  
  use analytics_db
  db.account_snapshots.find().limit(5).pretty()
  ```

---

## Architecture Highlights

- **OLTP vs OLAP:** Payment Service (fast transactions) vs Analytics Service (complex queries)
- **OAuth 2.0:** All services secured with JWT tokens from Keycloak
- **Centralized Config:** Config Server manages application configurations
- **Service Discovery:** Eureka tracks all service instances
- **API Gateway:** Single entry point with routing to microservices
- **MongoDB:** NoSQL for flexible analytics and payment data
- **Microservices:** 8 independent services, each with its own database

---

**Last Updated:** October 21, 2025  
**Spring Boot:** 3.1.5  
**Keycloak:** 23.0.7  
**MongoDB:** 7.0

---

## Support

If you encounter issues not covered in Troubleshooting:

1. Check service logs in the terminal where it's running
2. Verify all prerequisites are met
3. Ensure ports are not in use
4. Try restarting services in order
5. Check Docker logs: `docker logs finova-keycloak` or `docker logs finova-mongodb`

**Happy coding! 🚀**

