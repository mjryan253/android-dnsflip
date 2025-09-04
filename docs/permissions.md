# DNSFlip - Permission Setup Guide

DNSFlip requires the `WRITE_SECURE_SETTINGS` permission to modify DNS settings on Android. This permission cannot be granted through normal app installation and requires special setup. We provide multiple methods: **ADB (Recommended)**, **Root Access**, and **Alternative DNS Methods**.

## Method 1: Shizuku (Limited) ⚠️

**IMPORTANT**: Shizuku cannot grant the `WRITE_SECURE_SETTINGS` permission required for DNS operations. This is a fundamental system limitation. While Shizuku can grant other system permissions, it cannot provide the specific permission needed for DNS configuration changes.

**Status**: ❌ **Not Functional** - Shizuku approach cannot work for DNS operations

### What is Shizuku?
Shizuku is a system service that runs with root privileges and allows apps to access system APIs that normally require system-level permissions. It's widely used in the Android customization community and is considered safe and reliable.

### Installation Steps

#### Step 1: Install Shizuku
1. Download Shizuku from the official source:
   - **GitHub Releases**: https://github.com/RikkaApps/Shizuku/releases
   - **F-Droid**: Search for "Shizuku" in F-Droid
   - **Google Play**: Available on Google Play Store

2. Install the Shizuku APK on your device

#### Step 2: Start Shizuku Service
1. Open the Shizuku app
2. Choose your preferred method to start the service:

   **Option A: Root (Recommended)**
   - If you have root access, tap "Start via root"
   - Shizuku will start immediately

   **Option B: Wireless Debugging (Android 11+)**
   - Enable Developer Options on your device
   - Enable "Wireless debugging" in Developer Options
   - Tap "Start via wireless debugging" in Shizuku
   - Follow the on-screen instructions to connect via ADB

   **Option C: USB Debugging**
   - Enable Developer Options and USB Debugging
   - Connect your device to a computer with ADB
   - Run: `adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh`

#### Step 3: Grant Permission to DNSFlip
1. Once Shizuku is running, open DNSFlip
2. DNSFlip will automatically detect Shizuku and request the required permission
3. Grant the permission when prompted
4. You're ready to use DNSFlip!

### Advantages of Shizuku
- ✅ **User-Friendly**: No command line knowledge required
- ✅ **Persistent**: Stays running across reboots (with root)
- ✅ **Secure**: Well-established, trusted service
- ✅ **Automatic**: DNSFlip handles permission requests automatically
- ✅ **Community Support**: Large user base and active development

---

## Method 2: ADB (Recommended) 🔧

Since Shizuku cannot grant the required permission, ADB (Android Debug Bridge) is the recommended method for granting the `WRITE_SECURE_SETTINGS` permission.

### Prerequisites
- A computer with ADB installed
- USB cable or wireless debugging enabled
- Developer Options enabled on your Android device

### Installation Steps

#### Step 1: Enable Developer Options
1. Go to **Settings** → **About phone**
2. Tap **Build number** 7 times
3. You'll see "You are now a developer!"

#### Step 2: Enable USB Debugging
1. Go to **Settings** → **Developer options**
2. Enable **USB debugging**
3. Connect your device to your computer via USB

#### Step 3: Grant Permission via ADB
1. Open a terminal/command prompt on your computer
2. Run the following command:
   ```bash
   adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS
   ```

#### Step 4: Verify Permission
1. Open DNSFlip
2. Tap "Check Permission" button
3. You should see "Permission Granted ✓"

### Wireless Debugging (Android 11+)
If you prefer not to use a USB cable:

1. **Enable Wireless Debugging**:
   - Go to **Settings** → **Developer options**
   - Enable **Wireless debugging**
   - Note the IP address and port (e.g., 192.168.1.100:5555)

2. **Connect via ADB**:
   ```bash
   adb connect 192.168.1.100:5555
   adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS
   ```

3. **Disconnect when done**:
   ```bash
   adb disconnect 192.168.1.100:5555
   ```

### ADB Command Reference
```bash
# Grant permission to DNSFlip
adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS

# Revoke permission (if needed)
adb shell pm revoke com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS

# Check if permission is granted
adb shell dumpsys package com.mjryan253.dnsflip | grep permission
```

---

## Troubleshooting

### Shizuku Issues

**Shizuku won't start**
- Ensure you have root access or proper ADB setup
- Try restarting your device
- Check if Shizuku is compatible with your Android version

**Permission not granted through Shizuku**
- Make sure Shizuku service is running (green status in Shizuku app)
- Try restarting Shizuku service
- Check if DNSFlip is listed in Shizuku's app list

### ADB Issues

**"device not found" error**
- Ensure USB debugging is enabled
- Try different USB cable or port
- Install proper USB drivers for your device

**"permission denied" error**
- Make sure you're using the correct package name
- Try running ADB as administrator (Windows) or with sudo (Linux/Mac)
- Check if the device is properly connected

**Permission lost after reboot**
- This is normal behavior for ADB-granted permissions
- You'll need to re-run the ADB command after each reboot
- Consider using Shizuku for persistent permissions

### General Issues

**DNS changes not working**
- Verify permission is granted (check in DNSFlip)
- Try restarting your device
- Check if your device supports private DNS (Android 9+)

**App crashes or freezes**
- Clear app data and try again
- Ensure you're using the latest version of DNSFlip
- Check device compatibility (Android 9+ required)

---

## Security Notes

### Is it safe?
- **Shizuku**: Yes, it's a well-established, open-source project with a large user base
- **ADB**: Yes, you're only granting a specific permission to a specific app
- **DNS Changes**: The app only modifies DNS settings, which is a standard system feature

### What permissions does DNSFlip need?
- `WRITE_SECURE_SETTINGS`: Required to modify DNS settings
- No other permissions are required

### Privacy
- DNSFlip does not collect any personal data
- DNS settings are stored locally on your device
- No network requests are made except for DNS resolution

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
