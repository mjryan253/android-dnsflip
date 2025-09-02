DNSFlip: Updated MVP Task List

Phase 1: Project Foundation (✓ Complete)

    Project: Android Studio project created in a configured Git repository.

    Language: Kotlin.

    UI: Jetpack Compose.

    Storage: SharedPreferences.

Phase 2: Core Logic - The DNS Switching Mechanism

    Create DNSManager Class: A dedicated DNSManager.kt class to handle reading and writing DNS settings via Settings.Global.

    Implement DNS Read/Write Functions:

        getCurrentDnsMode(context)

        setDnsModeOff(context)

        setDnsModeOn(context, hostname)

    Add Permission to Manifest: Add <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" /> to AndroidManifest.xml.

Phase 3: User Interface (Jetpack Compose)

    Build Main Screen UI: Create a single screen with these composables:

        Text: To display the current DNS status (e.g., "Status: Automatic").

        OutlinedTextField: For the user to enter their desired DNS hostname.

        Switch or Button: The main toggle to enable/disable the custom DNS. The control should appear disabled if the permission is not granted.

        Button: A "Check Permission" button to re-check the status after the user has run the ADB command.

Phase 4: Data Persistence (SharedPreferences)

    Save Hostname: On a successful DNS change, save the hostname string to SharedPreferences.

    Load Hostname: When the app starts, load the saved hostname from SharedPreferences into the text field.

Phase 5: UX & Streamlined Onboarding

This is the most critical phase for making the app usable.

    Create Permission Logic: The app must check for the WRITE_SECURE_SETTINGS permission on startup and disable the main toggle if it's missing.

    Build the Onboarding/Instructions UI: Create a clear, unavoidable screen or dialog that appears if permission is not granted. This screen must include:

        A simple explanation of why the permission is needed.

        A "Copy ADB Command" button that programmatically puts the correct command on the clipboard: adb shell pm grant your.package.name.here android.permission.WRITE_SECURE_SETTINGS.

        A separate, expandable section with clear instructions for enabling and using Wireless Debugging for users on modern Android versions.

    (Optional but Recommended) Shizuku Integration:

        Add the Shizuku API library to your project.

        Implement logic to detect if Shizuku is installed and running.

        If Shizuku is available, request the permission through its API instead of showing the ADB instructions. This provides a much slicker experience for users in that ecosystem.

    Error Handling: Use a Snackbar or Toast to provide feedback (e.g., "DNS updated," or "Permission not granted").

    Create App Icon: Design a simple icon for DNSFlip.

Phase 6: Build & Release

    Code Cleanup: Add comments and ensure the code is readable.

    Generate Signed App Bundle: Prepare the app for release.

    Write Excellent README: Update the README.md on GitHub. This is your app's main documentation. It must clearly explain the feature set and provide comprehensive instructions for both the ADB method and the Shizuku method.