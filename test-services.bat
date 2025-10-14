@echo off
echo Testing Finova Retirement Microservices Setup...

echo.
echo === Step 1: Building all services ===
call mvn clean compile -DskipTests
if %ERRORLEVEL% neq 0 (
    echo BUILD FAILED! Please fix compilation errors.
    pause
    exit /b 1
)

echo.
echo === Step 2: Starting Eureka Server for 30 seconds ===
echo Starting Eureka Server...
start /min "Eureka Test" cmd /c "cd /d eureka-server && timeout /t 30 >nul && mvn spring-boot:run"

echo Waiting 30 seconds for Eureka to start...
timeout /t 30 /nobreak >nul

echo.
echo === Step 3: Testing Eureka Server ===
curl -f http://localhost:8761/actuator/health
if %ERRORLEVEL% eq 0 (
    echo ✓ Eureka Server is running successfully!
) else (
    echo ✗ Eureka Server is not responding
)

echo.
echo === Test Complete ===
echo.
echo If Eureka is running, you can:
echo 1. Visit http://localhost:8761 to see Eureka dashboard
echo 2. Run start-services.bat to start all services
echo 3. Kill the Eureka test window when done
echo.
pause
