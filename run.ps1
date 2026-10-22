# AquaSafe - Single Command PowerShell Launcher
# Usage: powershell -ExecutionPolicy Bypass -File .\run.ps1   OR   .\run.ps1

Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host "                AQUASAFE - WATER-BORNE DISEASE INTELLIGENCE                   " -ForegroundColor Cyan
Write-Host "           Big Data Analytics & Machine Learning Surveillance System          " -ForegroundColor Cyan
Write-Host "==============================================================================" -ForegroundColor Cyan
Write-Host ""

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

# 1. Check Java 17+
Write-Host "[*] Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVer = java -version 2>&1 | Out-String
    Write-Host "[+] Java detected:" -ForegroundColor Green
    $javaVer.Split("`n")[0..1] | ForEach-Object { Write-Host "    $_" -ForegroundColor Gray }
} catch {
    Write-Host "[ERROR] Java is not installed or not in PATH. Please install Java 17+." -ForegroundColor Red
    exit 1
}

# 2. Check or Build JAR
$JarPath = Join-Path $ScriptDir "backend\target\aquasafe-backend-1.0.0-SNAPSHOT.jar"
if (-not (Test-Path $JarPath)) {
    Write-Host "[*] Compiled JAR not found. Building with Maven..." -ForegroundColor Yellow
    Push-Location (Join-Path $ScriptDir "backend")
    & mvn clean package -DskipTests
    Pop-Location

    if (-not (Test-Path $JarPath)) {
        Write-Host "[ERROR] Maven build failed. Unable to locate $JarPath" -ForegroundColor Red
        exit 1
    }
    Write-Host "[+] Build succeeded." -ForegroundColor Green
}

# 3. Start Browser Opener background task
Write-Host "[*] Starting automated browser watcher..." -ForegroundColor Yellow
$watcherScript = {
    $attempts = 0
    while ($attempts -lt 40) {
        Start-Sleep -Seconds 2
        try {
            $resp = Invoke-RestMethod -Uri "http://localhost:8080/api/system/health" -TimeoutSec 2 -ErrorAction SilentlyContinue
            if ($resp.status -eq "UP") {
                Write-Host "`n[+] AquaSafe Server is UP and Healthy! Opening dashboard..." -ForegroundColor Green
                Start-Process "http://localhost:8080"
                break
            }
        } catch {}
        $attempts++
    }
}
$job = Start-Job -ScriptBlock $watcherScript

# 4. Banner & Server Startup
Write-Host ""
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host " Launching AquaSafe Application Server at: http://localhost:8080" -ForegroundColor Green
Write-Host " - Admin Account:   admin   / admin123" -ForegroundColor White
Write-Host " - Analyst Account: analyst / user123" -ForegroundColor White
Write-Host " - Press Ctrl+C in this console to stop the server" -ForegroundColor Gray
Write-Host "==============================================================================" -ForegroundColor Green
Write-Host ""

try {
    & java -jar $JarPath
} finally {
    Stop-Job $job -ErrorAction SilentlyContinue
    Remove-Job $job -ErrorAction SilentlyContinue
}
