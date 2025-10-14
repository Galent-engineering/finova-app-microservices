# Finova Retirement Planning - Microservices Architecture

## Overview
This project is a microservices-based retirement planning application that helps users manage their retirement accounts, contributions, and financial planning.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                       │
├─────────────────────────────────────────────────────────────┤
│  User Service    │  Account Service  │  Planning Service    │
│     (8081)       │      (8082)       │       (8083)         │
├─────────────────────────────────────────────────────────────┤
│           Service Discovery - Eureka (8761)                 │
│           Configuration Server (8888)                       │
└─────────────────────────────────────────────────────────────┘
```

## Services

### 1. **Eureka Server** (Port: 8761)
- Service discovery and registration
- Monitors health of all microservices

### 2. **API Gateway** (Port: 8080)
- Single entry point for client requests
- Load balancing and routing
- Security and authentication

### 3. **Config Server** (Port: 8888)
- Centralized configuration management
- Environment-specific configurations

### 4. **User Service** (Port: 8081)
- User authentication and authorization
- User profile management
- JWT token generation and validation

### 5. **Account Service** (Port: 8082)
- Retirement account management
- Contribution tracking
- Income source management

### 6. **Planning Service** (Port: 8083)
- Retirement calculators
- Financial planning tools
- Investment strategy recommendations

## Technology Stack

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Cloud 2022.0.4**
- **PostgreSQL** (Database per service)
- **Docker & Docker Compose**
- **Maven** (Build tool)

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL (if not using Docker)

### Running the Application

**Option 1: Quick Start (Recommended for Development)**

1. **Start databases (Optional - uses H2 in-memory by default)**
```bash
docker-compose -f docker-compose-simple.yml up -d
```

2. **Run the automated startup script**
```bash
# Windows
start-services.bat

# Manual startup (if script doesn't work)
mvn clean compile
# Then start services in separate terminals:
cd eureka-server && mvn spring-boot:run
cd config-server && mvn spring-boot:run  
cd user-service && mvn spring-boot:run
cd account-service && mvn spring-boot:run
cd planning-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
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
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081
- **Account Service**: http://localhost:8082
- **Planning Service**: http://localhost:8083
- **Config Server**: http://localhost:8888

## Development

### Adding New Services
1. Create new module in parent POM
2. Add service discovery client dependency
3. Configure service registration in application.yml
4. Implement business logic

### Configuration Management
All configurations are managed centrally via the Config Server. Service-specific configurations are stored in the `config-repo` directory.

## Testing
```bash
# Run all tests
mvn test

# Run specific service tests
cd user-service && mvn test
```

## Monitoring
- **Health Checks**: Available at `/actuator/health` for each service
- **Metrics**: Prometheus metrics at `/actuator/prometheus`
- **Service Discovery**: Monitor via Eureka dashboard

## Security
- JWT-based authentication
- Service-to-service communication security
- API Gateway handles authentication/authorization

## Database Design
Each service has its own database following the database-per-service pattern:
- **user_service_db**: User profiles and authentication
- **account_service_db**: Retirement accounts and contributions
- **planning_service_db**: Financial calculations and projections
