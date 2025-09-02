# DNSFlip - AI Memory & Project Documentation

## Project Overview
**DNSFlip** is an Android DNS switching app that makes changing DNS servers as simple as flipping a light switch. Built with Kotlin and Jetpack Compose, featuring OLED-optimized dark theme and comprehensive permission handling.

## User Instructions & Preferences
- **Default Theme**: Dark mode with OLED black (#000000) for battery optimization
- **Primary Permission Method**: Shizuku (recommended) with ADB fallback
- **UI Design**: Large light switch metaphor for DNS toggling
- **Target Platform**: Android native in Kotlin only
- **Documentation**: Comprehensive setup guides for both permission methods

## Project Structure & Locations
```
android-dnsflip/
├── Studio/                          # Android Studio project
│   ├── dnsflip/
│   │   ├── build.gradle.kts        # Build configuration
│   │   ├── proguard-rules.pro      # Release optimization rules
│   │   └── src/main/
│   │       ├── AndroidManifest.xml # App permissions & configuration
│   │       └── java/com/mjryan253/dnsflip/
│   │           ├── DNSManager.kt           # Core DNS switching logic
│   │           ├── ShizukuManager.kt       # Shizuku integration
│   │           ├── PreferencesManager.kt   # SharedPreferences handling
│   │           ├── MainActivity.kt         # Main UI and app logic
│   │           └── ui/
│   │               ├── components/
│   │               │   ├── LightSwitch.kt      # Custom switch component
│   │               │   └── OnboardingDialog.kt # Permission setup UI
│   │               └── theme/
│   │                   ├── Color.kt            # OLED-optimized colors
│   │                   ├── Theme.kt            # Dark theme configuration
│   │                   └── Type.kt             # Typography
├── docs/
│   ├── completed.md                 # Implementation phases documentation
│   ├── permissions.md               # Setup guide for Shizuku & ADB
│   └── ai-memory.md                 # This file
└── README.md                        # Comprehensive project documentation
```

## Implementation Phases Completed

### Phase 1: Project Foundation ✅
- Android Studio project created with Kotlin
- Jetpack Compose UI framework
- SharedPreferences for data persistence
- Basic project structure established

### Phase 2: Core Logic - DNS Switching Mechanism ✅
**DNSManager.kt** - Core DNS functionality:
- `getCurrentDnsMode(context)` - Returns current DNS mode
- `setDnsModeOff(context)` - Switches to automatic DNS
- `setDnsModeOn(context, hostname)` - Sets custom DNS
- `getCurrentDnsHostname(context)` - Gets current custom DNS hostname
- `hasDnsPermission(context)` - Checks permission status
- `getDnsStatusDescription(context)` - User-friendly status

**AndroidManifest.xml** - Added `WRITE_SECURE_SETTINGS` permission

### Phase 3: User Interface (Jetpack Compose) ✅
**Dark Theme with OLED Black**:
- True OLED black backgrounds (#000000)
- Custom color scheme in `Color.kt` and `Theme.kt`
- Status bar and navigation bar optimization
- Disabled dynamic colors for consistency

**LargeLightSwitch Component**:
- 280x140dp size with 120dp thumb
- Smooth 400ms animations
- Glowing effects and visual feedback
- Status text: "Custom DNS Active" / "System DNS Active"

**Main Screen UI**:
- DNS status display with user-friendly descriptions
- OutlinedTextField for DNS hostname input
- Permission check button with re-check functionality
- Permission instructions with ADB command copying
- Real-time integration with DNSManager

### Phase 4: Data Persistence (SharedPreferences) ✅
**PreferencesManager.kt** - Data persistence:
- `saveDnsHostname(hostname)` - Saves DNS hostname
- `loadDnsHostname()` - Loads saved hostname with defaults
- `saveLastDnsMode(mode)` - Saves last DNS mode
- `loadLastDnsMode()` - Loads last DNS mode
- Type-safe preference access with defaults

**Integration Features**:
- Automatic loading on app startup
- Smart loading logic (system vs saved preference)
- Success-based saving on DNS changes
- Debounced saving (1 second after typing)
- Mode tracking for future reference

### Phase 5: UX & Streamlined Onboarding ✅
**Shizuku Integration**:
- ShizukuManager.kt with real-time state monitoring
- Automatic detection of Shizuku installation and service status
- Seamless permission requests through Shizuku API
- Graceful fallback to ADB when Shizuku unavailable
- States: NOT_INSTALLED, NOT_RUNNING, PERMISSION_REQUIRED, PERMISSION_DENIED, READY, ERROR

**Comprehensive Onboarding UI**:
- OnboardingDialog.kt with method selection
- Real-time Shizuku status updates and contextual actions
- Detailed ADB instructions with step-by-step guidance
- Expandable sections for learning about each method
- One-click installation and permission granting

**Enhanced Permission Logic**:
- Smart permission management with multiple fallback options
- Priority system: Shizuku first, ADB fallback
- Real-time permission status updates
- Automatic onboarding dialog on app launch

**Snackbar Feedback System**:
- Modern, non-intrusive feedback replacing Toast messages
- Success/error messages with auto-dismiss
- Consistent styling with app's dark theme

**Documentation**:
- permissions.md with comprehensive setup guides
- Shizuku installation and setup instructions
- ADB guide with troubleshooting
- Security notes and device compatibility

### Phase 6: Build & Release ✅
**Code Cleanup**:
- Comprehensive comments and documentation added
- Enhanced class documentation with @author and @version tags
- Clear method descriptions and parameter documentation

**ProGuard Configuration**:
- Optimized proguard-rules.pro for release builds
- Proper obfuscation while keeping essential classes
- Shizuku API preservation
- Compose and Material3 class protection
- Log removal in release builds

**Build Configuration**:
- Debug and release build types configured
- Minification and resource shrinking enabled for release
- Signing configuration placeholder for production
- Application ID suffixes for debug builds

**Comprehensive README.md**:
- Feature overview with emojis and clear sections
- Quick start guide with prerequisites
- Detailed permission setup for both Shizuku and ADB
- Usage instructions with popular DNS servers
- Development setup and project structure
- Security and privacy information
- Troubleshooting guide
- Contributing guidelines

**App Icon**:
- Custom DNS-themed icon (ic_dnsflip.xml)
- Green circular design with switch metaphor
- DNS server lines and checkmark for active state
- Matches app's color scheme

## Technical Specifications

### Dependencies
```kotlin
// Core Android
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)

// UI Framework
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.ui)
implementation(libs.androidx.ui.graphics)
implementation(libs.androidx.ui.tooling.preview)
implementation(libs.androidx.material3)

// Shizuku Integration
implementation("dev.rikka.shizuku:api:13.1.0")
implementation("dev.rikka.shizuku:provider:13.1.0")
```

### Build Configuration
- **Compile SDK**: 36
- **Min SDK**: 33 (Android 13)
- **Target SDK**: 36
- **Kotlin**: 2.0.21
- **Gradle Plugin**: 8.11.1
- **Java Version**: 11

### Key Features Implemented
1. **OLED-Optimized Dark Theme** - True black backgrounds for battery savings
2. **Large Light Switch** - Intuitive DNS toggling with animations
3. **Shizuku Integration** - Seamless permission management
4. **ADB Fallback** - Manual permission setup for all users
5. **Data Persistence** - Automatic hostname saving and loading
6. **Comprehensive Onboarding** - Step-by-step setup guidance
7. **Real-Time Status** - Live DNS and permission monitoring
8. **Error Handling** - Graceful fallbacks and user feedback

## User Journey
1. **App Launch** - Automatic permission check
2. **No Permission** - Onboarding dialog appears
3. **Method Selection** - User chooses Shizuku or ADB
4. **Guided Setup** - Step-by-step instructions
5. **Permission Granted** - Automatic detection and activation
6. **Ready to Use** - Full functionality with visual confirmation

## Development Notes
- All code is Android native in Kotlin
- Jetpack Compose for modern UI
- Material Design 3 compliance
- Clean architecture with separation of concerns
- Comprehensive error handling
- No linting errors
- Production-ready code quality

## Next Steps for Continuation
1. **Testing** - Device testing on various Android versions
2. **Signing** - Set up release signing configuration
3. **Distribution** - Prepare for Play Store or APK distribution
4. **User Feedback** - Collect and implement user suggestions
5. **Feature Enhancements** - Additional DNS providers, advanced settings

## Memory Context for AI Agents
When continuing work on this project:
- User prefers dark theme with OLED optimization
- Shizuku is the primary permission method with ADB fallback
- All code must be Android native in Kotlin
- Comprehensive documentation is important
- User values clean, well-commented code
- Focus on user experience and intuitive design
- Maintain the light switch metaphor throughout

## Documentation Maintenance Protocol
**CRITICAL**: When making any changes or completing phases, always update these files:

### 1. ai-memory.md (This File)
- Update implementation phases as they are completed
- Add new technical specifications and dependencies
- Document any new user preferences or instructions
- Update file locations and project structure changes
- Maintain comprehensive project memory for AI continuity

### 2. completed.md
- Append new phase completions with detailed implementation notes
- Include technical details, key features, and user experience improvements
- Document code quality improvements and architectural decisions
- Maintain chronological record of all completed work

### 3. README.md
- Update feature lists and technical specifications
- Add new setup instructions or troubleshooting information
- Update dependencies and build requirements
- Maintain user-facing documentation accuracy

### Documentation Update Process:
1. **Complete Implementation** - Finish the actual code/feature work
2. **Update ai-memory.md** - Add to implementation phases and technical specs
3. **Update completed.md** - Append detailed phase completion notes
4. **Update README.md** - Update user-facing documentation if needed
5. **Verify Consistency** - Ensure all documentation is synchronized

### Documentation Standards:
- Use consistent formatting and structure
- Include technical details in ai-memory.md and completed.md
- Keep user-facing content in README.md clear and accessible
- Always include file locations and implementation details
- Document any new user preferences or project requirements

## File Locations for Quick Reference
- **Main App Logic**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/MainActivity.kt`
- **DNS Management**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/DNSManager.kt`
- **Shizuku Integration**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/ShizukuManager.kt`
- **Data Persistence**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/PreferencesManager.kt`
- **UI Components**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/ui/components/`
- **Theme System**: `Studio/dnsflip/src/main/java/com/mjryan253/dnsflip/ui/theme/`
- **Build Config**: `Studio/dnsflip/build.gradle.kts`
- **ProGuard Rules**: `Studio/dnsflip/proguard-rules.pro`
- **App Manifest**: `Studio/dnsflip/src/main/AndroidManifest.xml`
- **Documentation**: `docs/` folder
- **Project README**: `README.md`

This document serves as the complete memory and reference for the DNSFlip project, enabling seamless continuation of work in future AI chat sessions.
