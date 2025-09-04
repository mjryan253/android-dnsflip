# DNSFlip 🔄

## TL;DR
**DNSFlip** is an Android app that lets you switch DNS servers with a single tap - like flipping a light switch. It toggles between your device's default DNS and custom DNS servers (like Cloudflare's 1.1.1.1) instantly, with a beautiful OLED-optimized dark theme.

---

## Executive Summary

DNSFlip is a modern, minimalist Android application designed to simplify DNS server management. Built with Jetpack Compose and Material Design 3, it transforms the complex process of changing DNS settings into an intuitive, simple two-tap experience.

**Core Value Proposition:**
- **Simplicity**: Simple two-tap DNS switching with a large, intuitive light switch interface
- **Performance**: OLED-optimized dark theme for battery efficiency and modern aesthetics
**Accessibility**: Zero-permission setup using Android's native VpnService.
- **Reliability**: Real-time status monitoring and automatic settings persistence

**Target Users:**
- Privacy-conscious users who want to switch between DNS providers
- Developers and power users who need quick DNS configuration changes
- Anyone seeking faster, more secure internet connections

**Key Differentiators:**
- **Light Switch Metaphor**: Unlike traditional settings apps, DNSFlip uses a familiar light switch interface
- **No Root or ADB Required**: Uses Android's standard VpnService to redirect DNS traffic.
- **Simple Switching**: No app restarts or device reboots required after initial setup.
- **Modern UI**: Built with the latest Android development tools for a premium feel

**Technical Architecture:**
- Kotlin-based with Jetpack Compose for modern UI
- Implements a local `VpnService` to intercept and redirect DNS traffic.
- Comprehensive testing suite planned with unit, integration, and UI tests
- Material Design 3 compliance with custom OLED optimization

**Current Implementation Status:**
- ✅ UI and core app structure implemented.
- ✅ OLED-optimized dark theme with true black backgrounds
- ✅ Custom light switch component with smooth animations
- ✅ Data persistence with SharedPreferences
- 🔄 **Pivoting to a VpnService implementation for DNS handling.**
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

### 🔐 **Simple Permission Handling**
- **VpnService Integration** - Uses Android's built-in VPN functionality.
- **No ADB or Root** - Does not require special permissions like `WRITE_SECURE_SETTINGS`.
- **Standard User Consent** - Works by asking the user for a standard VPN connection permission.

### 💾 **Data Persistence**
- **Automatic Saving** - Remembers your preferred DNS server
- **Smart Loading** - Restores settings on app restart
- **Debounced Input** - Saves changes as you type

## 🚀 Quick Start

### Prerequisites
- **Android 9+** (API level 28 or higher)

### Installation

1. **Download** the latest APK from [Releases](https://github.com/your-username/dnsflip/releases)
2. **Install** the APK on your Android device
3. **Open the app and grant VPN permission** when prompted.
4. **Start Flipping** DNS servers!

## 🔧 Permission Setup

DNSFlip uses Android's `VpnService` to change your DNS servers. This method does not require root, `adb`, or any special permissions.

When you first activate a custom DNS, the app will ask for permission to create a VPN connection. This is a standard Android dialog. Simply tap "OK" to grant the permission. The app uses this VPN to route your DNS queries to the server of your choice. No other traffic is affected.

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
- **🟢 Custom DNS Active** - The DNSFlip VPN is running and directing traffic.
- **⚪ System DNS Active** - The DNSFlip VPN is turned off.
- **🔴 Permission Required** - The app needs you to grant VPN permission.

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
├── VpnManager.kt              # Core DNS switching logic via VpnService
├── DnsVpnService.kt           # The VpnService implementation
├── PreferencesManager.kt      # Data persistence
├── MainActivity.kt            # Main UI and app logic
└── ui/
    ├── components/
    │   └── LightSwitch.kt     # Custom switch component
    └── theme/
        ├── Color.kt           # OLED-optimized colors
        ├── Theme.kt           # Dark theme configuration
        └── Type.kt            # Typography

Studio/dnsflip/src/test/java/com/mjryan253/dnsflip/
├── VpnManagerTest.kt          # DNS functionality unit tests
├── PreferencesManagerTest.kt  # Data persistence unit tests
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
- **Kotlin Coroutines** - Asynchronous programming

### Testing Dependencies
- **JUnit 4** - Testing framework
- **MockK** - Mocking library for Kotlin
- **Compose Testing** - UI testing framework
- **AndroidX Test** - Instrumented testing
- **Coroutines Test** - Async testing support

## 🔒 Security & Privacy

### Permissions
DNSFlip requires the `BIND_VPN_SERVICE` permission to function.

**No other permissions are requested or required.**

### Privacy
- ✅ **No data collection** - App doesn't collect any personal information
- ✅ **Local storage only** - All settings stored on your device
- ✅ **Local VPN** - The VPN service runs only on your device to redirect DNS traffic. Your other network traffic is not logged or modified.
- ✅ **Open source** - Full source code available for review

### Safety
- **VpnService** - Uses the standard Android `VpnService` API, which is secure and managed by the OS.
- **DNS Changes** - The app only directs DNS queries to the server you choose.

## 🐛 Troubleshooting

### Common Issues

**"Permission not granted"**
- When you toggle the switch for the first time, Android will ask for permission to create a VPN. You must accept this to use the app.

**"DNS changes not working"**
- Ensure the switch in the app is "On".
- Check that you have granted VPN permission. You can revoke and re-grant this from your phone's Settings menu (usually under Network -> VPN).
- Restart your device.

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