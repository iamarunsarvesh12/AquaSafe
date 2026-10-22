@echo off
setlocal enabledelayedexpansion

title AquaSafe - Water-Borne Disease Risk Intelligence System
cls

echo ==============================================================================
echo                 AQUASAFE - WATER-BORNE DISEASE INTELLIGENCE                   
echo            Big Data Analytics & Machine Learning Surveillance System          
echo ==============================================================================
echo.

:: 1. Navigate to script directory (Project Root)
cd /d "%~dp0"

:: 2. Check Java Environment
echo [*] Checking Java Runtime Environment...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not found in system PATH.
    echo Please install Java 17 or higher from https://adoptium.net/
    pause
    exit /b 1
)
echo [+] Java detected successfully.

:: 3. Check / Build Backend JAR
set "JAR_PATH=backend\target\aquasafe-backend-1.0.0-SNAPSHOT.jar"
if not exist "%JAR_PATH%" (
    echo [*] Compiled JAR not found. Building AquaSafe backend with Maven...
    cd backend
    call mvn clean package -DskipTests
    cd ..
    if not exist "%JAR_PATH%" (
        echo [ERROR] Build failed. Please inspect Maven build output above.
        pause
        exit /b 1
    )
    echo [+] Build completed successfully.
)

:: 4. Launch Health Checker & Auto-Browser Opener in background
echo [*] Initializing automated browser launcher helper...
start /b "" powershell -NoProfile -ExecutionPolicy Bypass -Command "$attempts=0; while ($attempts -lt 40) { Start-Sleep -Seconds 2; try { $res = Invoke-RestMethod -Uri 'http://localhost:8080/api/system/health' -TimeoutSec 2; if ($res.status -eq 'UP') { Write-Host '`n[+] AquaSafe Backend is UP and Healthy! Launching browser...'; Start-Process 'http://localhost:8080'; break; } } catch {} $attempts++; }"

:: 5. Start the Spring Boot Backend Server
echo.
echo ==============================================================================
echo  Starting AquaSafe Application Server on http://localhost:8080
echo  - Default Administrator: admin / admin123
echo  - Public Health Analyst: analyst / user123
echo  - Press Ctrl+C at any time to gracefully terminate
echo ==============================================================================
echo.

java -jar "%JAR_PATH%"

pause
