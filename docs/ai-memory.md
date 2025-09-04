# AI Memory - DNSFlip Project

## Project Overview
DNSFlip is an Android application that allows users to switch between system DNS and custom DNS servers. The app uses a local `VpnService` to route DNS traffic, making it compatible with non-rooted devices without requiring `adb` commands.

## Current Status: Phase 22 - Architectural Pivot to VpnService

### What Was Accomplished in Phase 22
- **Architectural Refactoring**: The application's core logic was completely refactored, moving away from a non-functional `WRITE_SECURE_SETTINGS` approach to a standard, reliable `VpnService` implementation.
- **Problem Resolution**: This change fixed the fundamental issue where the app could not function without manual `adb` commands, as Shizuku was unable to grant the required permission.
- **Code Changes**:
    - **Removed**: `ShizukuManager.kt`, `OnboardingDialog.kt`, and all related tests and documentation.
    - **Added**: `DnsVpnService.kt` to manage the VPN tunnel and `VpnManager.kt` to control the service.
    - **Updated**: `MainActivity.kt` was overhauled to integrate with the new `VpnManager`.
    - **Updated**: `AndroidManifest.xml` was updated with the `BIND_VPN_SERVICE` permission.
    - **Updated**: All documentation (`README.md`, `docs/permissions.md`, `docs/completed.md`, `docs/next-steps.md`) was updated to reflect the new architecture.

### What's Now Fully Functional
1.  **One-Tap DNS Switching**: Users can enable and disable custom DNS with a single tap.
2.  **No Root or ADB**: The app works for any user on Android 9+ without special permissions.
3.  **Real-time Status**: The UI accurately reflects whether the custom DNS is active or inactive.
4.  **Simplified UX**: The permission model is now a standard, one-time VPN consent dialog.

### Next Steps Required
- See `docs/next-steps.md` for the post-refactoring roadmap, which includes thorough testing and release preparation.

### Completed Work
- See `docs/completed.md` for a detailed summary of the work completed in this phase.

---

## Previous Phases

### Phase 21 & 20: Discovery of Shizuku Limitation
- **Discovery**: It was determined that the Shizuku-based approach was fundamentally flawed, as Shizuku cannot grant the `WRITE_SECURE_SETTINGS` permission required for the old method of DNS switching. This discovery necessitated the architectural pivot completed in Phase 22.

*(Older phase history has been archived.)*
