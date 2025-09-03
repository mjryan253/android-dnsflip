@echo off
echo ========================================
echo DNSFlip Rapid Deployment & Testing Script
echo ========================================
echo.

:: Check if we're in the right directory
if not exist "Studio\gradlew.bat" (
    echo ERROR: This script must be run from the android-dnsflip root directory
    echo Current directory: %CD%
    echo.
    echo Please navigate to the android-dnsflip directory and run this script again.
    pause
    exit /b 1
)

:: Navigate to Studio directory
cd Studio

echo [1/6] Checking ADB connection...
adb devices >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: ADB not found or not working
    echo Please ensure Android SDK is installed and ADB is in your PATH
    pause
    exit /b 1
)

:: Get list of connected devices
for /f "tokens=1" %%i in ('adb devices ^| findstr "device$"') do set DEVICE_ID=%%i

if "%DEVICE_ID%"=="" (
    echo ERROR: No Android devices found
    echo.
    echo Please ensure:
    echo - Virtual device is running
    echo - USB debugging is enabled (for physical devices)
    echo - Device is connected and authorized
    echo.
    echo Starting emulator...
    emulator -list-avds
    echo.
    echo To start an emulator, run: emulator -avd [AVD_NAME]
    pause
    exit /b 1
)

echo Found device: %DEVICE_ID%
echo.

echo [2/6] Cleaning previous build...
call gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

echo [3/6] Building debug APK...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo [4/6] Installing APK on device...
call gradlew installDebug
if %errorlevel% neq 0 (
    echo ERROR: Installation failed
    pause
    exit /b 1
)

echo [5/6] Starting DNSFlip app...
adb shell am start -n com.mjryan253.dnsflip/.MainActivity
if %errorlevel% neq 0 (
    echo WARNING: Could not start app automatically
    echo Please start the app manually from the device
)

echo [6/6] Opening logcat for monitoring...
echo.
echo ========================================
echo DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo Device: %DEVICE_ID%
echo App: DNSFlip (com.mjryan253.dnsflip)
echo.
echo Next steps:
echo 1. The app should now be running on your device
echo 2. Logcat is open below for debugging
echo 3. Test DNS switching functionality
echo 4. Check error messages and troubleshooting features
echo.
echo Press Ctrl+C to stop logcat monitoring
echo.

:: Start logcat with DNSFlip-specific filtering
adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V ActivityManager:I System.err:W

echo.
echo Deployment script completed.
pause
