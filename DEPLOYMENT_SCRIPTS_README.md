# DNSFlip Rapid Deployment Scripts

This directory contains scripts to rapidly deploy and test the DNSFlip app on your local virtual device or connected Android device.

## 🚀 Quick Start

### Windows Users
```cmd
# Run the batch script from the android-dnsflip root directory
deploy-and-test.bat
```

### Windows PowerShell Users (Recommended)
```powershell
# Run the PowerShell script from the android-dnsflip root directory
.\deploy-and-test.ps1

# Or with specific options
.\deploy-and-test.ps1 -SkipClean -NoLogcat
```

### Unix/Linux/macOS Users
```bash
# Make the script executable (first time only)
chmod +x deploy-and-test.sh

# Run the script from the android-dnsflip root directory
./deploy-and-test.sh
```

## 📱 Prerequisites

1. **Android SDK** installed and in your PATH
2. **ADB (Android Debug Bridge)** accessible
3. **Virtual device running** or **physical device connected** with USB debugging enabled
4. **Java Development Kit (JDK)** for Gradle builds

## 🔧 Script Features

### What Each Script Does
1. **Directory Validation** - Ensures you're in the correct project directory
2. **ADB Connection Check** - Verifies ADB is working and devices are connected
3. **Build Clean** - Cleans previous build artifacts
4. **APK Build** - Builds the debug APK using Gradle
5. **Installation** - Installs the APK on your device
6. **App Launch** - Automatically starts the DNSFlip app
7. **Logcat Monitoring** - Opens filtered logcat for debugging

### PowerShell Script Options
The PowerShell script supports several command-line options:

```powershell
# Skip specific steps
.\deploy-and-test.ps1 -SkipClean        # Skip cleaning previous build
.\deploy-and-test.ps1 -SkipBuild        # Skip building APK
.\deploy-and-test.ps1 -SkipInstall      # Skip installing APK
.\deploy-and-test.ps1 -SkipStart        # Skip starting the app
.\deploy-and-test.ps1 -NoLogcat         # Skip opening logcat

# Specify a specific device
.\deploy-and-test.ps1 -DeviceId "emulator-5554"

# Combine options
.\deploy-and-test.ps1 -SkipClean -SkipBuild -NoLogcat
```

## 🎯 Use Cases

### Rapid Development Testing
```powershell
# Full deployment cycle for testing changes
.\deploy-and-test.ps1
```

### Quick Reinstall (skip build)
```powershell
# Reinstall existing APK without rebuilding
.\deploy-and-test.ps1 -SkipClean -SkipBuild
```

### Debug Mode Only
```powershell
# Install and start app, then manually check logs
.\deploy-and-test.ps1 -NoLogcat
```

### Device-Specific Deployment
```powershell
# Deploy to a specific device when multiple are connected
.\deploy-and-test.ps1 -DeviceId "your-device-id"
```

## 📊 Logcat Filtering

The scripts automatically open logcat with DNSFlip-specific filtering:

```bash
adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V ActivityManager:I System.err:W
```

This shows:
- **DNSManager**: All DNS operation logs
- **ShizukuManager**: Permission and Shizuku-related logs
- **MainActivity**: UI and app lifecycle logs
- **ActivityManager**: App launch and system logs
- **System.err**: Error logs

## 🐛 Troubleshooting

### Common Issues

#### "ADB not found"
- Ensure Android SDK is installed
- Add `platform-tools` to your PATH
- Restart your terminal after PATH changes

#### "No Android devices found"
- Start your virtual device: `emulator -avd [AVD_NAME]`
- Enable USB debugging on physical devices
- Check device connection: `adb devices`

#### "Build failed"
- Check Java/JDK installation
- Verify Gradle wrapper exists: `Studio/gradlew`
- Check for compilation errors in the code

#### "Installation failed"
- Ensure device has enough storage
- Check if app is already installed (uninstall first if needed)
- Verify device is unlocked and accessible

### Manual Commands

If scripts fail, you can run commands manually:

```bash
# Navigate to Studio directory
cd Studio

# Clean and build
./gradlew clean
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Start app
adb shell am start -n com.mjryan253.dnsflip/.MainActivity

# Monitor logs
adb logcat -s DNSManager:V ShizukuManager:V MainActivity:V
```

## 🔄 Development Workflow

### Typical Development Cycle
1. **Make code changes** in Android Studio or your editor
2. **Run deployment script** to test changes
3. **Check app behavior** on device
4. **Review logs** for any issues
5. **Iterate** and repeat

### Testing DNS Functionality
1. **Grant Shizuku permissions** when prompted
2. **Test DNS switching** using the light switch
3. **Check error messages** if operations fail
4. **Use troubleshooting button** for diagnostics
5. **Verify DNS changes** in system settings

## 📁 File Structure

```
android-dnsflip/
├── deploy-and-test.bat          # Windows batch script
├── deploy-and-test.ps1          # Windows PowerShell script
├── deploy-and-test.sh           # Unix/Linux/macOS script
├── DEPLOYMENT_SCRIPTS_README.md # This file
├── Studio/                      # Android project directory
│   ├── gradlew                  # Gradle wrapper
│   ├── gradlew.bat             # Gradle wrapper (Windows)
│   └── dnsflip/                # App source code
└── ...
```

## 🚨 Important Notes

- **Always run scripts from the android-dnsflip root directory**
- **Ensure your virtual device is running before executing scripts**
- **Scripts will automatically detect and use the first available device**
- **Logcat monitoring continues until you press Ctrl+C**
- **PowerShell script provides the most features and better error handling**

## 🤝 Contributing

If you improve these scripts or add new features:
1. Update this README with new options/features
2. Test on multiple platforms if possible
3. Keep backward compatibility when possible
4. Add helpful error messages and user guidance

---

**Happy Testing! 🧪**

These scripts should significantly speed up your development and testing workflow for DNSFlip.
