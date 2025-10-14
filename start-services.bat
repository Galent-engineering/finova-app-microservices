@echo off
echo Starting Finova Retirement Microservices...

echo.
echo === Step 1: Starting databases (optional) ===
echo If you want to use PostgreSQL databases, run:
echo docker-compose -f docker-compose-simple.yml up -d
echo.
echo Otherwise, services will use H2 in-memory databases.
echo Press any key to continue...
pause >nul

echo.
echo === Step 2: Building all services ===
echo Building parent project...
call mvn clean compile -DskipTests

echo.
echo === Step 3: Starting services in order ===

echo Starting Eureka Server (Service Discovery)...
start "Eureka Server" cmd /k "cd /d eureka-server && mvn spring-boot:run"
echo Waiting 60 seconds for Eureka to fully start...
timeout /t 60 /nobreak >nul

echo Starting Config Server (runs independently)...
start "Config Server" cmd /k "cd /d config-server && mvn spring-boot:run"
echo Waiting 20 seconds for Config Server to start...
timeout /t 20 /nobreak >nul

echo Starting User Service...
start "User Service" cmd /k "cd /d user-service && mvn spring-boot:run"
echo Waiting 20 seconds for User Service to start...
timeout /t 20 /nobreak >nul

echo Starting Account Service...
start "Account Service" cmd /k "cd /d account-service && mvn spring-boot:run"
echo Waiting 20 seconds for Account Service to start...
timeout /t 20 /nobreak >nul

echo Starting Planning Service...
start "Planning Service" cmd /k "cd /d planning-service && mvn spring-boot:run"
echo Waiting 20 seconds for Planning Service to start...
timeout /t 20 /nobreak >nul

echo Starting API Gateway (Final)...
start "API Gateway" cmd /k "cd /d api-gateway && mvn spring-boot:run"

echo.
echo === All services are starting up ===
echo.
echo Service URLs:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8081/api/users/health
echo - Account Service: http://localhost:8082/actuator/health
echo - Planning Service: http://localhost:8083/actuator/health
echo - Config Server: http://localhost:8888/actuator/health
echo.
echo Wait 2-3 minutes for all services to fully register with Eureka.
echo Check Eureka dashboard to see all services registered.

echo.
echo To stop all services, close all the opened command windows.
echo.
pause
