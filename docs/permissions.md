# DNSFlip - Permission Setup Guide

DNSFlip uses Android's `VpnService` API to change your DNS settings. This is the modern, recommended approach that does not require root access, `adb` commands, or any complex setup.

## The VpnService Method (Standard)

When you enable a custom DNS for the first time, DNSFlip will ask for permission to create a VPN connection. This is a standard Android system dialog that allows the app to handle your network traffic.

**Status**: ✅ **Fully Functional & Recommended**

### How It Works
1.  **You Toggle the Switch**: In the app, you enter your desired DNS server and flip the switch to "On".
2.  **Android Asks for Permission**: A system pop-up will appear, asking you to allow DNSFlip to set up a VPN connection.
3.  **You Grant Permission**: Tap "OK" or "Allow". This is a one-time setup.
4.  **DNS is Flipped**: DNSFlip establishes a **local** VPN on your device. This VPN's only job is to route your DNS queries to the custom server you selected. All other internet traffic continues normally and is not logged, modified, or rerouted.

### Advantages of the VpnService Method
- ✅ **No Root or ADB**: Works on any standard Android device (version 9+).
- ✅ **User-Friendly**: Just a single tap on a system dialog.
- ✅ **Secure**: Uses the official Android `VpnService` API, which is managed and secured by the operating system.
- ✅ **Privacy-Focused**: The VPN is local to your device. Your traffic is not sent to a third-party server.

---

## Deprecated Methods (No Longer Used)

The following methods were previously considered but are **not used** in the current version of DNSFlip because the `VpnService` method is superior in every way.

### Shizuku / ADB (❌ Deprecated)

Previous versions attempted to use `adb` or the Shizuku service to grant the `WRITE_SECURE_SETTINGS` permission. This approach was flawed because:
-   It required a connection to a computer (`adb`) or a complex setup (Shizuku).
-   `WRITE_SECURE_SETTINGS` is a powerful, protected permission that Shizuku cannot grant, making that approach non-functional.
-   Changes made via `adb` are often temporary and lost on reboot.

The new `VpnService` approach solves all of these problems, providing a seamless and reliable experience without compromising security.

---

## Troubleshooting

### VPN Permission Issues

**"Permission not granted" or "Connection failed"**
- When you toggle the switch for the first time, Android will ask for permission to create a VPN. You must accept this dialog to use the app. If you deny it, the app cannot function.
- If you don't see the dialog, try revoking the permission from your phone's settings and trying again. Go to `Settings > Apps > DNSFlip > Permissions` and ensure VPN access is not denied. On some devices, this is under `Settings > Network & Internet > VPN`.

**DNS changes don't seem to work**
-   Ensure the switch in the app is in the "On" position.
-   Verify that the VPN icon (usually a small key) is visible in your status bar. If it's not there, the VPN is not running.
-   Try turning the switch off and on again in the app.
-   As a last resort, restart your device.

---

## Security Notes

### Is it safe?
-   **Yes**. The app uses the standard Android `VpnService` API. This creates a **local** VPN on your device for the sole purpose of redirecting DNS requests. Your internet traffic is not sent to an external server.

### What permissions does DNSFlip need?
-   `BIND_VPN_SERVICE`: This is required to create the local VPN for DNS redirection.
-   No other special permissions are required.

### Privacy
-   DNSFlip does not collect any personal data.
-   All settings are stored locally on your device.
-   The VPN service does not log or inspect your network traffic. It only routes DNS queries.

---

## Support

If you encounter issues:

1. **Check this documentation** for common solutions
2. **Try both methods** (Shizuku and ADB) to see which works better
3. **Check device compatibility** (Android 9+ required)
4. **Contact support** with specific error messages and device information

### Device Compatibility
- **Minimum**: Android 9 (API level 28)
- **Recommended**: Android 11+ for wireless debugging
- **Root**: Not required, but recommended for Shizuku persistence

### Popular DNS Servers
- **Cloudflare**: `1.1.1.1` (Fast, privacy-focused)
- **Google**: `8.8.8.8` (Reliable, widely used)
- **Quad9**: `9.9.9.9` (Security-focused, blocks malicious domains)
- **OpenDNS**: `208.67.222.222` (Family-friendly filtering options)
