# DNSFlip 🔄

## TL;DR
**DNSFlip** is an Android app that lets you switch DNS servers with a single tap - like flipping a light switch. It toggles between your device's default DNS and custom DNS servers (like Cloudflare's 1.1.1.1) instantly, with a beautiful OLED-optimized dark theme.

---

## Executive Summary

DNSFlip is a modern, minimalist Android application designed to simplify DNS server management. Built with Jetpack Compose and Material Design 3, it transforms the complex process of changing DNS settings into an intuitive, simple two-tap experience.

**Core Value Proposition:**
- **Simplicity**: Simple two-tap DNS switching with a large, intuitive light switch interface
- **Performance**: OLED-optimized dark theme for battery efficiency and modern aesthetics
- **Accessibility**: Comprehensive permission setup with Shizuku integration and ADB fallback
- **Reliability**: Real-time status monitoring and automatic settings persistence

**Target Users:**
- Privacy-conscious users who want to switch between DNS providers
- Developers and power users who need quick DNS configuration changes
- Anyone seeking faster, more secure internet connections

**Key Differentiators:**
- **Light Switch Metaphor**: Unlike traditional settings apps, DNSFlip uses a familiar light switch interface
- **Smart Permissions**: Seamless integration with Shizuku for automatic permission management
- **Simple Switching**: No app restarts or device reboots required after initial setup
- **Modern UI**: Built with the latest Android development tools for a premium feel

**Technical Architecture:**
- Kotlin-based with Jetpack Compose for modern UI
- Shizuku API integration for system-level permissions (implementation in progress)
- Comprehensive testing suite planned with unit, integration, and UI tests
- Material Design 3 compliance with custom OLED optimization

**Current Implementation Status:**
- ✅ Core DNS switching functionality implemented
- ✅ OLED-optimized dark theme with true black backgrounds
- ✅ Custom light switch component with smooth animations
- ✅ Data persistence with SharedPreferences
- 🔄 Shizuku integration in development (build issues being resolved)
- 🔄 Testing suite structure established, full implementation in progress

---

**DNSFlip** is a beautiful, modern Android app that makes switching DNS servers as simple as flipping a light switch. Built with Jetpack Compose and optimized for OLED displays, it provides an intuitive way to toggle between system DNS and custom DNS servers.

![DNSFlip Logo](docs/assets/logo.png)

## ✨ Features

### 🎨 **Beautiful UI**
- **OLED-Optimized Dark Theme** - True black backgrounds for maximum battery savings
- **Large Light Switch** - Intuitive toggle for DNS switching
- **Material Design 3** - Modern, accessible interface
- **Smooth Animations** - Polished user experience

### 🔧 **DNS Management**
- **One-Tap Switching** - Toggle between system and custom DNS instantly
- **Popular DNS Servers** - Pre-configured with Cloudflare, Google, Quad9, and more
- **Custom DNS Support** - Enter any DNS server you prefer
- **Real-Time Status** - Live updates of current DNS configuration

### 🔐 **Smart Permission Handling**
- **Shizuku Integration** - Seamless permission management (recommended)
- **ADB Fallback** - Manual permission setup for all users
- **Comprehensive Onboarding** - Step-by-step setup guidance
- **Automatic Detection** - Smart permission status monitoring

### 💾 **Data Persistence**
- **Automatic Saving** - Remembers your preferred DNS server
- **Smart Loading** - Restores settings on app restart
- **Debounced Input** - Saves changes as you type

## 🚀 Quick Start

### Prerequisites
- **Android 9+** (API level 28 or higher)
- **WRITE_SECURE_SETTINGS** permission (see setup below)

### Installation

1. **Download** the latest APK from [Releases](https://github.com/your-username/dnsflip/releases)
2. **Install** the APK on your Android device
3. **Grant Permission** using one of the methods below
4. **Start Flipping** DNS servers!

## 🔧 Permission Setup

DNSFlip requires special permission to modify DNS settings. Choose your preferred method:

### Method 1: Shizuku (Recommended) 🚀

**Shizuku** provides the smoothest experience with automatic permission management.

1. **Install Shizuku** from [GitHub Releases](https://github.com/RikkaApps/Shizuku/releases)
2. **Start Shizuku** service (root, wireless debugging, or USB debugging)
3. **Open DNSFlip** - it will automatically detect Shizuku and request permission
4. **Grant permission** when prompted

**Benefits:**
- ✅ User-friendly interface
- ✅ No command line needed
- ✅ Persistent permissions
- ✅ Trusted by the community

### Method 2: ADB (Manual) 🔧

For users who prefer manual control or don't want to use Shizuku.

1. **Enable Developer Options** (Settings → About phone → Tap "Build number" 7 times)
2. **Enable USB Debugging** (Settings → Developer options → USB debugging)
3. **Connect device** to computer with ADB
4. **Run command:**
   ```bash
   adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS
   ```

**For Wireless Debugging (Android 11+):**
```bash
adb connect YOUR_DEVICE_IP:5555
adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS
```

## 📱 Usage

### Basic Operation
1. **Open DNSFlip** - The app will check permissions automatically
2. **Enter DNS Server** - Type your preferred DNS (e.g., `1.1.1.1`, `dns.google`)
3. **Toggle Switch** - Tap the large light switch to enable/disable custom DNS
4. **Enjoy** - Your DNS settings are applied instantly!

### Popular DNS Servers
- **Cloudflare**: `1.1.1.1` - Fast, privacy-focused
- **Google**: `8.8.8.8` - Reliable, widely used
- **Quad9**: `9.9.9.9` - Security-focused, blocks malicious domains
- **OpenDNS**: `208.67.222.222` - Family-friendly filtering options

### Status Indicators
- **🟢 Custom DNS Active** - Your custom DNS server is being used
- **⚪ System DNS Active** - Using your device's default DNS
- **🔴 Permission Required** - Setup needed before use

## 🛠️ Development

### Building from Source

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/dnsflip.git
   cd dnsflip
   ```

2. **Open in Android Studio** (Arctic Fox or later)

3. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device:**
   ```bash
   ./gradlew installDebug
   ```

### Testing

DNSFlip includes a comprehensive test suite covering unit tests, integration tests, and UI tests.

**Run all tests:**
```bash
./gradlew test connectedAndroidTest
```

**Run unit tests only:**
```bash
./gradlew test
```

**Run instrumented tests:**
```bash
./gradlew connectedAndroidTest
```

**Run with coverage:**
```bash
./gradlew testDebugUnitTestCoverage
```

For detailed testing information, see [Testing Guide](docs/testing.md).

### Project Structure
```
Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/
├── DNSManager.kt              # Core DNS switching logic
├── ShizukuManager.kt          # Shizuku integration
├── PreferencesManager.kt      # Data persistence
├── MainActivity.kt            # Main UI and app logic
└── ui/
    ├── components/
    │   ├── LightSwitch.kt     # Custom switch component
    │   └── OnboardingDialog.kt # Permission setup UI
    └── theme/
        ├── Color.kt           # OLED-optimized colors
        ├── Theme.kt           # Dark theme configuration
        └── Type.kt            # Typography

Studio/dnsflip/src/test/java/com/mjryan253/dnsflip/
├── DNSManagerTest.kt          # DNS functionality unit tests
├── PreferencesManagerTest.kt  # Data persistence unit tests
├── ShizukuManagerTest.kt      # Shizuku integration unit tests
└── ui/theme/ThemeTest.kt      # Theme system unit tests

Studio/dnsflip/src/androidTest/java/com/mjryan253/dnsflip/
├── DNSManagerIntegrationTest.kt      # DNS integration tests
├── PreferencesManagerIntegrationTest.kt # Preferences integration tests
├── MainActivityUITest.kt             # UI component tests
└── ExampleInstrumentedTest.kt        # Basic instrumented tests
```

### Dependencies
- **AndroidX Core KTX** - Android extensions
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system
- **Shizuku API** - System permission access
- **Kotlin Coroutines** - Asynchronous programming

### Testing Dependencies
- **JUnit 4** - Testing framework
- **MockK** - Mocking library for Kotlin
- **Compose Testing** - UI testing framework
- **AndroidX Test** - Instrumented testing
- **Coroutines Test** - Async testing support

## 🔒 Security & Privacy

### Permissions
DNSFlip only requires:
- `WRITE_SECURE_SETTINGS` - To modify DNS settings

**No other permissions are requested or required.**

### Privacy
- ✅ **No data collection** - App doesn't collect any personal information
- ✅ **Local storage only** - All settings stored on your device
- ✅ **No network requests** - Except for DNS resolution
- ✅ **Open source** - Full source code available for review

### Safety
- **Shizuku** - Well-established, trusted system service
- **ADB** - Standard Android development tool
- **DNS Changes** - Standard system feature, not invasive

## 🐛 Troubleshooting

### Common Issues

**"Permission not granted"**
- Ensure you've completed the setup process
- Try the "Check Permission" button
- Re-run ADB command if using manual method

**"DNS changes not working"**
- Verify permission is granted
- Restart your device
- Check if your device supports private DNS (Android 9+)

**"Shizuku not working"**
- Ensure Shizuku service is running
- Try restarting Shizuku
- Check Shizuku compatibility with your Android version

### Getting Help
1. **Check documentation** in the `docs/` folder
2. **Review troubleshooting** in `docs/permissions.md`
3. **Open an issue** on GitHub with:
   - Device model and Android version
   - Steps to reproduce the problem
   - Screenshots if applicable

## 📋 Requirements

### Device Compatibility
- **Minimum**: Android 9 (API level 28)
- **Recommended**: Android 11+ for wireless debugging
- **Architecture**: ARM64, ARM32, x86, x86_64

### System Requirements
- **Root**: Not required (but recommended for Shizuku persistence)
- **Storage**: ~10MB for app installation
- **RAM**: Minimal impact, lightweight app

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Shizuku** - For providing seamless system API access
- **Jetpack Compose** - For the modern UI toolkit
- **Material Design** - For the design system
- **Android Community** - For feedback and contributions

## 📞 Support

- **GitHub Issues** - Bug reports and feature requests
- **Documentation** - Comprehensive setup guides
- **Community** - Join discussions and get help

---

**Made with ❤️ for the Android community**

*DNSFlip - Making DNS switching as simple as flipping a switch*