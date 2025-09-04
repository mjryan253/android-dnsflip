# DNSFlip Rapid Deployment & Testing Script
# PowerShell version for Windows

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
    exit 1
}

# Navigate to Studio directory
Set-Location Studio
Write-Host "Changed to Studio directory: $PWD" -ForegroundColor Green
Write-Host ""

Write-Host "[1/6] Checking ADB connection..." -ForegroundColor Green
try {
    $adbVersion = adb version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "ADB not found"
    }
    Write-Host "ADB found and working" -ForegroundColor Green
} catch {
    Write-Host "ERROR: ADB not found or not working" -ForegroundColor Red
    Write-Host "Please ensure Android SDK is installed and ADB is in your PATH" -ForegroundColor Yellow
    exit 1
}

# Get list of connected devices
if ($DeviceId -eq "") {
    Write-Host "Checking for connected devices..." -ForegroundColor Green
    $devices = adb devices | Where-Object { $_ -match "device$" }
    if ($devices) {
        $DeviceId = ($devices[0] -split "\s+")[0]
        Write-Host "Found device: $DeviceId" -ForegroundColor Green
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
    exit 1
}

Write-Host "Using device: $DeviceId" -ForegroundColor Green
Write-Host ""

if (-not $SkipClean) {
    Write-Host "[2/6] Cleaning previous build..." -ForegroundColor Green
    Write-Host "Running: .\gradlew.bat clean" -ForegroundColor Gray
    & .\gradlew.bat clean
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Clean failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
    Write-Host "Clean completed successfully" -ForegroundColor Green
} else {
    Write-Host "[2/6] Skipping clean (SkipClean flag set)" -ForegroundColor Yellow
}

if (-not $SkipBuild) {
    Write-Host "[3/6] Building debug APK..." -ForegroundColor Green
    Write-Host "Running: .\gradlew.bat assembleDebug" -ForegroundColor Gray
    & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
    Write-Host "Build completed successfully" -ForegroundColor Green
    
    # Verify APK was created
    $apkPath = "dnsflip\build\outputs\apk\debug\dnsflip-debug.apk"
    if (Test-Path $apkPath) {
        $apkSize = (Get-Item $apkPath).Length / 1MB
        Write-Host "APK created: $apkPath ($([math]::Round($apkSize, 2)) MB)" -ForegroundColor Green
    } else {
        Write-Host "WARNING: APK not found at expected location: $apkPath" -ForegroundColor Yellow
    }
} else {
    Write-Host "[3/6] Skipping build (SkipBuild flag set)" -ForegroundColor Yellow
}

if (-not $SkipInstall) {
    Write-Host "[4/6] Installing APK on device..." -ForegroundColor Green
    Write-Host "Running: .\gradlew.bat installDebug" -ForegroundColor Gray
    & .\gradlew.bat installDebug
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Installation failed with exit code: $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }
    Write-Host "Installation completed successfully" -ForegroundColor Green
    
    # Verify app is installed
    Write-Host "Verifying app installation..." -ForegroundColor Green
    $installedApp = adb shell pm list packages | Where-Object { $_ -match "com.mjryan253.dnsflip" }
    if ($installedApp) {
        Write-Host "App verified as installed: $installedApp" -ForegroundColor Green
    } else {
        Write-Host "WARNING: Could not verify app installation" -ForegroundColor Yellow
    }
} else {
    Write-Host "[4/6] Skipping installation (SkipInstall flag set)" -ForegroundColor Yellow
}

if (-not $SkipStart) {
    Write-Host "[5/6] Starting DNSFlip app..." -ForegroundColor Green
    Write-Host "Running: adb shell am start -n com.mjryan253.dnsflip.debug/.MainActivity" -ForegroundColor Gray
    adb shell am start -n com.mjryan253.dnsflip.debug/.MainActivity
    if ($LASTEXITCODE -ne 0) {
        Write-Host "WARNING: Could not start app automatically (exit code: $LASTEXITCODE)" -ForegroundColor Yellow
        Write-Host "Please start the app manually from the device" -ForegroundColor Yellow
    } else {
        Write-Host "App started successfully" -ForegroundColor Green
    }
} else {
    Write-Host "[5/6] Skipping app start (SkipStart flag set)" -ForegroundColor Yellow
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
Write-Host "Deployment script completed successfully!" -ForegroundColor Green
