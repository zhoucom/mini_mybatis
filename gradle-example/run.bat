@echo off
echo Mini MyBatis Gradle Example - Windows Startup Script
echo ====================================================

REM Check Java environment
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found. Please install JDK 8+
    pause
    exit /b 1
)

REM Check Gradle environment  
if not exist "gradlew.bat" (
    echo ERROR: Gradle Wrapper not found
    pause
    exit /b 1
)

echo Java environment check passed
echo Gradle environment check passed

echo.
echo Building Gradle project...
call gradlew.bat clean build

if %errorlevel% equ 0 (
    echo Project build successful
) else (
    echo Project build failed
    pause
    exit /b 1
)

echo.
echo Starting application...
echo Application will start at http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo Health Check: http://localhost:8080/api/products/health
echo.
echo Press Ctrl+C to stop the application
echo.

REM Start application
call gradlew.bat bootRun

pause 