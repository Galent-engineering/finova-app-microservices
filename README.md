# Finova Retirement Planning - Microservices Architecture

## Overview
This project is a microservices-based retirement planning application that helps users manage their retirement accounts, contributions, and financial planning.

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
│    Keycloak         │  User Service    │  Account Service  │  Planning       │
│   Auth Server       │     (8081)       │      (8082)       │  Service        │
│     (8080)          │                  │                   │   (8083)        │
├─────────────────────────────────────────────────────────────────────────────┤
│              Service Discovery - Eureka (8761)                              │
│              Configuration Server (8888)                                    │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 🔄 **Request Flow:**
```
Browser → API Gateway (9080) → Service Discovery → Microservice (8081-8083)
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

### 8. **Frontend Application** (Port: 8000)
- **Modern, responsive web interface**
- **Routes through API Gateway (9080)**
- **Interactive retirement planning dashboard**
- **Real-time service monitoring** (including gateway)
- **Account and contribution management**
- **Planning tools and calculators**
- **CORS-enabled** for gateway integration

## Technology Stack

**Backend:**
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Cloud 2022.0.4**
- **PostgreSQL** (Database per service)
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
```

### 📊 **Monitoring & Testing**
- **Service Tester**: http://localhost:8000/test-services.html
- **Gateway Health**: http://localhost:9080/actuator/health  
- **Active Routes**: http://localhost:9080/actuator/gateway/routes
- **Fallback Testing**: http://localhost:9080/fallback/user-service

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

### ⚙️ System Monitoring
- **Microservices Status**: Real-time health checks for all services
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
│   ├── style.css               # Complete styling system
│   ├── script.js               # Application logic
│   ├── test-services.html      # Service connectivity tester
│   ├── start-frontend.bat      # Windows startup script
│   └── README.md               # Frontend documentation
├── api-gateway/                # API Gateway service
├── user-service/               # User management service
├── account-service/            # Account management service
├── planning-service/           # Planning tools service
├── eureka-server/              # Service discovery
├── config-server/              # Configuration management
├── docker-compose.yml         # Full Docker setup
├── docker-compose-simple.yml  # Database-only Docker
├── start-services.bat          # Windows startup script
└── pom.xml                     # Parent Maven configuration
```

## Testing

**Backend Testing:**
```bash
# Run all tests
mvn test

# Run specific service tests
cd user-service && mvn test
```

**Frontend Testing:**
```bash
# Manual testing checklist
1. Open http://localhost:8000
2. Verify all service status cards show correct status
3. Test navigation between sections
4. Test account management forms
5. Test planning calculators
6. Verify responsive design on different devices

# Use service connection tester
Open http://localhost:8000/test-services.html
```

## Monitoring
- **Health Checks**: Available at `/actuator/health` for each service
- **Metrics**: Prometheus metrics at `/actuator/prometheus`
- **Service Discovery**: Monitor via Eureka dashboard

## Security
- JWT-based authentication
- Service-to-service communication security
- API Gateway handles authentication/authorization

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

## Database Design
Each service has its own database following the database-per-service pattern:
- **user_service_db**: User profiles and authentication
- **account_service_db**: Retirement accounts and contributions
- **planning_service_db**: Financial calculations and projections

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
