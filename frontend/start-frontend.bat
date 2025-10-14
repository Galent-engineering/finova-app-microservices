@echo off
echo ========================================
echo Finova Retirement - Frontend Startup
echo ========================================
echo.
echo Starting local web server on port 8000...
echo.
echo Frontend will be available at:
echo   http://localhost:8000
echo.
echo Make sure your microservices are running:
echo   - API Gateway: localhost:8080
echo   - User Service: localhost:8081  
echo   - Account Service: localhost:8082
echo   - Planning Service: localhost:8083
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

REM Try Python 3 first
where python >nul 2>&1
if %errorlevel% == 0 (
    echo Using Python to start server...
    python -m http.server 8000
    goto :end
)

REM Try Python 2 as fallback
where python2 >nul 2>&1  
if %errorlevel% == 0 (
    echo Using Python 2 to start server...
    python2 -m SimpleHTTPServer 8000
    goto :end
)

REM If no Python, show instructions
echo ERROR: Python not found!
echo.
echo Please install Python or use one of these alternatives:
echo   - Node.js: npx http-server
echo   - PHP: php -S localhost:8000
echo   - Or open index.html directly in your browser
echo.

:end
pause
