# API Gateway - Finova Retirement Microservices

## Overview
Enhanced API Gateway with service discovery, load balancing, rate limiting, and comprehensive monitoring.

## Features
- ✅ **Eureka Service Discovery** - Dynamic service registration and discovery
- ✅ **Load Balancing** - Automatic load balancing across service instances  
- ✅ **Rate Limiting** - Redis-based rate limiting per client IP
- ✅ **CORS Support** - Cross-origin resource sharing configuration
- ✅ **Fallback Handling** - Service-specific fallback responses
- ✅ **Request/Response Headers** - Custom headers for tracking
- ✅ **Monitoring** - Actuator endpoints for health and metrics
- ✅ **Logging** - Comprehensive request/response logging

## Service Configuration
**Port**: 9080 (changed from 8080 due to Keycloak conflict)

### Service Routes
- **User Service**: `/api/users/**`, `/api/auth/**` → `lb://user-service`
- **Account Service**: `/api/accounts/**`, `/api/contributions/**`, `/api/income-sources/**`, `/api/dashboard/**` → `lb://account-service`  
- **Planning Service**: `/api/planning/**`, `/api/calculators/**` → `lb://planning-service`

### Rate Limiting (per IP)
- **User Service**: 10 requests/second, burst 20
- **Account Service**: 15 requests/second, burst 30
- **Planning Service**: 5 requests/second, burst 10

## Testing the Gateway

### Quick Test Commands
```bash
# Test gateway health
curl http://localhost:9080/actuator/health

# Test service routing (when services are running)
curl http://localhost:9080/api/users/health
curl http://localhost:9080/api/accounts/health  
curl http://localhost:9080/api/planning/health

# Test fallback endpoints
curl http://localhost:9080/fallback/user-service
curl http://localhost:9080/fallback/account-service
curl http://localhost:9080/fallback/planning-service
```

### Monitoring Endpoints
- Gateway: http://localhost:9080
- Health Check: http://localhost:9080/actuator/health
- Gateway Routes: http://localhost:9080/actuator/gateway/routes
- Prometheus Metrics: http://localhost:9080/actuator/prometheus

## Running the Enhanced Gateway

### 1. Start Prerequisites
```bash
# Start Eureka Server first
cd ../eureka-server
mvn spring-boot:run
```

### 2. Start API Gateway  
```bash
cd api-gateway
mvn clean compile
mvn spring-boot:run
```

### 3. Optional: Start Redis for Rate Limiting
```bash
# Windows: Download Redis or use Docker
docker run -d -p 6379:6379 redis:alpine

# The gateway works without Redis - rate limiting will be disabled
```

## Architecture Benefits

### Enhanced Features
- **Dynamic Service Discovery** - No hardcoded service URLs
- **Load Balancing** - Automatic distribution across service instances
- **Rate Protection** - Prevents service overload
- **CORS Enabled** - Frontend integration ready
- **Fallback Responses** - Graceful degradation when services are down
- **Request Tracking** - Headers for debugging and monitoring
- **Comprehensive Logging** - File and console logging with rotation
