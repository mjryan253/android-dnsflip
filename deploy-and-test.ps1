# DNSFlip Rapid Deployment & Testing Script
# PowerShell version for better compatibility and features

param(
    [switch]$SkipClean,
    [switch]$SkipBuild,
    [switch]$SkipInstall,
    [switch]$SkipStart,
    [switch]$NoLogcat,
    [string]$DeviceId = ""
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DNSFlip Rapid Deployment & Testing Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "Studio\gradlew.bat")) {
    Write-Host "ERROR: This script must be run from the android-dnsflip root directory" -ForegroundColor Red
    Write-Host "Current directory: $PWD" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Please navigate to the android-dnsflip directory and run this script again." -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

# Navigate to Studio directory
Set-Location Studio

Write-Host "[1/6] Checking ADB connection..." -ForegroundColor Green
try {
    $adbVersion = adb version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "ADB not found"
    }
} catch {
    Write-Host "ERROR: ADB not found or not working" -ForegroundColor Red
    Write-Host "Please ensure Android SDK is installed and ADB is in your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

# Get list of connected devices
if ($DeviceId -eq "") {
    $devices = adb devices | Where-Object { $_ -match "device$" }
    if ($devices) {
        $DeviceId = ($devices[0] -split "\s+")[0]
    }
}

if ($DeviceId -eq "") {
    Write-Host "ERROR: No Android devices found" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please ensure:" -ForegroundColor Yellow
    Write-Host "- Virtual device is running" -ForegroundColor Yellow
    Write-Host "- USB debugging is enabled (for physical devices)" -ForegroundColor Yellow
    Write-Host "- Device is connected and authorized" -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "Available AVDs:" -ForegroundColor Cyan
    emulator -list-avds
    Write-Host ""
    Write-Host "To start an emulator, run: emulator -avd [AVD_NAME]" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
    exit 1
}

Write-Host "Found device: $DeviceId" -ForegroundColor Green
Write-Host ""

if (-not $SkipClean) {
    Write-Host "[2/6] Cleaning previous build..." -ForegroundColor Green
    & .\gradlew clean
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Clean failed" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        exit 1
    }
}

if (-not $SkipBuild) {
    Write-Host "[3/6] Building debug APK..." -ForegroundColor Green
    & .\gradlew assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        exit 1
    }
}

if (-not $SkipInstall) {
    Write-Host "[4/6] Installing APK on device..." -ForegroundColor Green
    & .\gradlew installDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Installation failed" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        exit 1
    }
}

if (-not $SkipStart) {
    Write-Host "[5/6] Starting DNSFlip app..." -ForegroundColor Green
    adb shell am start -n com.mjryan253.dnsflip/.MainActivity
    if ($LASTEXITCODE -ne 0) {
        Write-Host "WARNING: Could not start app automatically" -ForegroundColor Yellow
        Write-Host "Please start the app manually from the device" -ForegroundColor Yellow
    }
}

Write-Host "[6/6] Deployment complete!" -ForegroundColor Green
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DEPLOYMENT COMPLETE!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Device: $DeviceId" -ForegroundColor White
Write-Host "App: DNSFlip (com.mjryan253.dnsflip)" -ForegroundColor White
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. The app should now be running on your device" -ForegroundColor White
Write-Host "2. Test DNS switching functionality" -ForegroundColor White
Write-Host "3. Check error messages and troubleshooting features" -ForegroundColor White
Write-Host "4. Use the troubleshooting button for diagnostics" -ForegroundColor White
Write-Host ""

if (-not $NoLogcat) {
    Write-Host "Opening logcat for monitoring..." -ForegroundColor Green
    Write-Host "Press Ctrl+C to stop logcat monitoring" -ForegroundColor Yellow
    Write-Host ""
    
    # Start logcat with DNSFlip-specific filtering
    adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V ActivityManager:I System.err:W
} else {
    Write-Host "Logcat monitoring skipped. To monitor logs manually, run:" -ForegroundColor Cyan
    Write-Host "adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V" -ForegroundColor White
}

Write-Host ""
Write-Host "Deployment script completed." -ForegroundColor Green
Read-Host "Press Enter to continue"
