package com.mjryan253.dnsflip

import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * DNSManager handles all DNS-related operations through Android's Settings.Global API
 * 
 * This class provides a clean interface for reading and writing DNS settings,
 * with comprehensive error handling and user-friendly status reporting.
 * 
 * Key Features:
 * - DNS mode management (automatic, custom, off)
 * - Permission validation and error handling
 * - User-friendly status descriptions
 * - Comprehensive logging for debugging
 * - Input validation and sanitization
 * - Detailed error reporting for troubleshooting
 * 
 * @author DNSFlip Team
 * @version 1.1
 * @since Android 9 (API 28)
 */
class DNSManager {
    
    companion object {
        private const val TAG = "DNSManager"
        
        // DNS mode constants
        private const val PRIVATE_DNS_MODE = "private_dns_mode"
        private const val PRIVATE_DNS_SPECIFIER = "private_dns_specifier"
        
        // DNS mode values
        private const val PRIVATE_DNS_MODE_OFF = "off"
        private const val PRIVATE_DNS_MODE_OPPORTUNISTIC = "opportunistic"
        private const val PRIVATE_DNS_MODE_STRICT = "hostname"
    }
    
    /**
     * Enhanced result class for DNS operations with detailed error information
     */
    data class DnsOperationResult(
        val success: Boolean,
        val errorMessage: String? = null,
        val errorCode: String? = null,
        val details: String? = null
    )
    
    /**
     * Gets the current DNS mode from system settings
     * @param context Application context
     * @return DNS mode string: "off", "automatic", "custom", "unknown", or "error"
     */
    fun getCurrentDnsMode(context: Context): String {
        return try {
            Log.d(TAG, "Getting current DNS mode")
            val dnsMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val dnsSpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            
            Log.d(TAG, "DNS mode: $dnsMode, specifier: $dnsSpecifier")
            
            when (dnsMode) {
                PRIVATE_DNS_MODE_OFF -> {
                    Log.d(TAG, "DNS mode: off")
                    "off"
                }
                PRIVATE_DNS_MODE_OPPORTUNISTIC -> {
                    Log.d(TAG, "DNS mode: automatic")
                    "automatic"
                }
                PRIVATE_DNS_MODE_STRICT -> {
                    if (dnsSpecifier.isNullOrEmpty()) {
                        Log.w(TAG, "DNS mode: strict but no specifier, treating as automatic")
                        "automatic"
                    } else {
                        Log.d(TAG, "DNS mode: custom with specifier: $dnsSpecifier")
                        "custom"
                    }
                }
                else -> {
                    Log.w(TAG, "Unknown DNS mode: $dnsMode, treating as automatic")
                    "automatic"
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when getting DNS mode - permission denied", e)
            "unknown"
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when getting DNS mode", e)
            "error"
        }
    }
    
    /**
     * Gets the current DNS hostname if custom DNS is enabled
     * @param context Application context
     * @return DNS hostname or null if not set
     */
    fun getCurrentDnsHostname(context: Context): String? {
        return try {
            Log.d(TAG, "Getting current DNS hostname")
            val hostname = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            Log.d(TAG, "Current DNS hostname: $hostname")
            hostname
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when getting DNS hostname - permission denied", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when getting DNS hostname", e)
            null
        }
    }
    
    /**
     * Sets DNS mode to automatic (uses system default DNS) with detailed error reporting
     * @param context Application context
     * @return DnsOperationResult with success status and detailed error information
     */
    fun setDnsModeOffDetailed(context: Context): DnsOperationResult {
        return try {
            Log.d(TAG, "Setting DNS mode to automatic (off)")
            
            // First, verify we can read current settings
            val currentMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val currentSpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            Log.d(TAG, "Current DNS settings - mode: $currentMode, specifier: $currentSpecifier")
            
            // Set mode to opportunistic (automatic)
            val modeResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_OPPORTUNISTIC)
            if (!modeResult) {
                Log.e(TAG, "Failed to set DNS mode to opportunistic - putString returned false")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "Failed to set DNS mode to automatic",
                    errorCode = "PUT_STRING_FAILED",
                    details = "Settings.Global.putString returned false for private_dns_mode"
                )
            }
            
            // Clear the specifier
            val specifierResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, "")
            if (!specifierResult) {
                Log.e(TAG, "Failed to clear DNS specifier - putString returned false")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "Failed to clear DNS specifier",
                    errorCode = "SPECIFIER_CLEAR_FAILED",
                    details = "Settings.Global.putString returned false for private_dns_specifier"
                )
            }
            
            // Verify the changes were applied
            val verifyMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val verifySpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            
            if (verifyMode != PRIVATE_DNS_MODE_OPPORTUNISTIC) {
                Log.e(TAG, "DNS mode verification failed - expected: ${PRIVATE_DNS_MODE_OPPORTUNISTIC}, got: $verifyMode")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "DNS mode change verification failed",
                    errorCode = "VERIFICATION_FAILED",
                    details = "Mode verification failed - expected: ${PRIVATE_DNS_MODE_OPPORTUNISTIC}, got: $verifyMode"
                )
            }
            
            Log.i(TAG, "Successfully set DNS mode to automatic - verified mode: $verifyMode, specifier: $verifySpecifier")
            DnsOperationResult(success = true)
            
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when setting DNS mode to automatic - permission denied", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Permission denied: Cannot modify DNS settings",
                errorCode = "SECURITY_EXCEPTION",
                details = "SecurityException: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when setting DNS mode to automatic", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Unexpected error occurred",
                errorCode = "UNEXPECTED_ERROR",
                details = "Exception: ${e.javaClass.simpleName} - ${e.message}"
            )
        }
    }
    
    /**
     * Sets DNS mode to use a custom DNS hostname with detailed error reporting
     * @param context Application context
     * @param hostname The DNS hostname to use (e.g., "1.1.1.1", "dns.google")
     * @return DnsOperationResult with success status and detailed error information
     */
    fun setDnsModeOnDetailed(context: Context, hostname: String): DnsOperationResult {
        if (hostname.isBlank()) {
            Log.w(TAG, "Cannot set DNS mode: hostname is blank")
            return DnsOperationResult(
                success = false,
                errorMessage = "Hostname cannot be blank",
                errorCode = "INVALID_HOSTNAME",
                details = "Hostname is empty or contains only whitespace"
            )
        }
        
        val trimmedHostname = hostname.trim()
        Log.d(TAG, "Setting DNS mode to custom with hostname: $trimmedHostname")
        
        return try {
            // First, verify we can read current settings
            val currentMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val currentSpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            Log.d(TAG, "Current DNS settings - mode: $currentMode, specifier: $currentSpecifier")
            
            // Set mode to strict (custom)
            val modeResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_STRICT)
            if (!modeResult) {
                Log.e(TAG, "Failed to set DNS mode to strict - putString returned false")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "Failed to set DNS mode to custom",
                    errorCode = "MODE_SET_FAILED",
                    details = "Settings.Global.putString returned false for private_dns_mode"
                )
            }
            
            // Set the specifier
            val specifierResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, trimmedHostname)
            if (!specifierResult) {
                Log.e(TAG, "Failed to set DNS specifier - putString returned false")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "Failed to set DNS hostname",
                    errorCode = "SPECIFIER_SET_FAILED",
                    details = "Settings.Global.putString returned false for private_dns_specifier"
                )
            }
            
            // Verify the changes were applied
            val verifyMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val verifySpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            
            if (verifyMode != PRIVATE_DNS_MODE_STRICT) {
                Log.e(TAG, "DNS mode verification failed - expected: ${PRIVATE_DNS_MODE_STRICT}, got: $verifyMode")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "DNS mode change verification failed",
                    errorCode = "MODE_VERIFICATION_FAILED",
                    details = "Mode verification failed - expected: ${PRIVATE_DNS_MODE_STRICT}, got: $verifyMode"
                )
            }
            
            if (verifySpecifier != trimmedHostname) {
                Log.e(TAG, "DNS specifier verification failed - expected: $trimmedHostname, got: $verifySpecifier")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "DNS hostname change verification failed",
                    errorCode = "SPECIFIER_VERIFICATION_FAILED",
                    details = "Specifier verification failed - expected: $trimmedHostname, got: $verifySpecifier"
                )
            }
            
            Log.i(TAG, "Successfully set DNS mode to custom with hostname: $trimmedHostname - verified mode: $verifyMode, specifier: $verifySpecifier")
            DnsOperationResult(success = true)
            
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when setting DNS mode to custom - permission denied", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Permission denied: Cannot modify DNS settings",
                errorCode = "SECURITY_EXCEPTION",
                details = "SecurityException: ${e.message}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when setting DNS mode to custom", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Unexpected error occurred",
                errorCode = "UNEXPECTED_ERROR",
                details = "Exception: ${e.javaClass.simpleName} - ${e.message}"
            )
        }
    }
    
    /**
     * Sets DNS mode to automatic (uses system default DNS)
     * @param context Application context
     * @return true if successful, false otherwise
     */
    fun setDnsModeOff(context: Context): Boolean {
        return setDnsModeOffDetailed(context).success
    }
    
    /**
     * Sets DNS mode to use a custom DNS hostname
     * @param context Application context
     * @param hostname The DNS hostname to use (e.g., "1.1.1.1", "dns.google")
     * @return true if successful, false otherwise
     */
    fun setDnsModeOn(context: Context, hostname: String): Boolean {
        return setDnsModeOnDetailed(context, hostname).success
    }
    
    /**
     * Checks if the app has the required permission to modify DNS settings
     * @param context Application context
     * @return true if permission is granted, false otherwise
     */
    fun hasDnsPermission(context: Context): Boolean {
        return try {
            Log.d(TAG, "Checking DNS permission")
            // Try to read the current DNS mode - if this succeeds, we have permission
            val dnsMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            Log.i(TAG, "DNS permission check successful, current mode: $dnsMode")
            true
        } catch (e: SecurityException) {
            Log.e(TAG, "DNS permission check failed - SecurityException", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "DNS permission check failed - unexpected error", e)
            false
        }
    }
    
    /**
     * Enhanced permission check that tests actual DNS write operations
     * @return DnsOperationResult with detailed permission test results
     */
    fun testDnsWritePermission(context: Context): DnsOperationResult {
        return try {
            Log.d(TAG, "Testing DNS write permission with actual operations")
            
            // Get current DNS mode to restore later
            val currentMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val currentSpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            
            Log.d(TAG, "Current DNS settings before test - mode: $currentMode, specifier: $currentSpecifier")
            
            // Try to write a test value temporarily
            val testMode = "opportunistic"
            val writeResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, testMode)
            
            if (!writeResult) {
                Log.w(TAG, "DNS write permission test failed - putString returned false")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "Cannot write DNS settings",
                    errorCode = "WRITE_TEST_FAILED",
                    details = "Settings.Global.putString returned false during permission test"
                )
            }
            
            // Verify the write actually worked
            val verifyValue = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            
            if (verifyValue != testMode) {
                Log.w(TAG, "DNS write permission test failed - verification failed. Expected: $testMode, Got: $verifyValue")
                return DnsOperationResult(
                    success = false,
                    errorMessage = "DNS write verification failed",
                    errorCode = "WRITE_VERIFICATION_FAILED",
                    details = "Write test failed - expected: $testMode, got: $verifyValue"
                )
            }
            
            // Restore original values
            Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, currentMode ?: "opportunistic")
            
            if (currentSpecifier != null) {
                Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, currentSpecifier)
            }
            
            Log.i(TAG, "DNS write permission test successful - can modify DNS settings")
            DnsOperationResult(success = true)
            
        } catch (e: SecurityException) {
            Log.w(TAG, "DNS write permission test failed - SecurityException", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Permission denied: Cannot modify DNS settings",
                errorCode = "SECURITY_EXCEPTION",
                details = "SecurityException during write test: ${e.message}"
            )
        } catch (e: Exception) {
            Log.w(TAG, "DNS write permission test failed - unexpected error", e)
            DnsOperationResult(
                success = false,
                errorMessage = "Unexpected error during permission test",
                errorCode = "UNEXPECTED_ERROR",
                details = "Exception during write test: ${e.javaClass.simpleName} - ${e.message}"
            )
        }
    }
    
    /**
     * Gets a user-friendly description of the current DNS status
     * @param context Application context
     * @return Human-readable DNS status
     */
    fun getDnsStatusDescription(context: Context): String {
        val mode = getCurrentDnsMode(context)
        val hostname = getCurrentDnsHostname(context)
        
        val description = when (mode) {
            "off" -> "Status: Off (No DNS protection)"
            "automatic" -> "Status: Automatic (System DNS)"
            "custom" -> "Status: Custom (${hostname ?: "Unknown"})"
            "unknown" -> "Status: Unknown (Permission required)"
            "error" -> "Status: Error (Check permissions)"
            else -> "Status: Unknown"
        }
        
        Log.d(TAG, "DNS status description: $description")
        return description
    }
    

    /**
     * Get detailed DNS configuration information
     * @param context Application context
     * @return Detailed DNS configuration string
     */
    fun getDetailedDnsInfo(context: Context): String {
        return try {
            val mode = getCurrentDnsMode(context)
            val hostname = getCurrentDnsHostname(context)
            val hasPermission = hasDnsPermission(context)
            
            val builder = StringBuilder()
            builder.append("Mode: $mode\n")
            
            when (mode) {
                "custom" -> {
                    builder.append("Custom DNS: $hostname\n")
                    builder.append("Status: Active")
                }
                "automatic" -> {
                    builder.append("System DNS: Automatic\n")
                    builder.append("Status: Active")
                }
                "off" -> {
                    builder.append("System DNS: Disabled\n")
                    builder.append("Status: Inactive")
                }
                "opportunistic" -> {
                    builder.append("System DNS: Opportunistic\n")
                    builder.append("Status: Active")
                }
                else -> {
                    builder.append("Status: Unknown")
                }
            }
            
            builder.append("\nPermission: ${if (hasPermission) "Granted" else "Denied"}")
            
            builder.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting detailed DNS info", e)
            "Error: Unable to get DNS information"
        }
    }
    
    /**
     * Get comprehensive troubleshooting information
     * @param context Application context
     * @return Detailed troubleshooting information
     */
    fun getTroubleshootingInfo(context: Context): String {
        val builder = StringBuilder()
        builder.append("=== DNS TROUBLESHOOTING INFO ===\n\n")
        
        try {
            // Current DNS state
            val mode = getCurrentDnsMode(context)
            val hostname = getCurrentDnsHostname(context)
            builder.append("Current DNS State:\n")
            builder.append("- Mode: $mode\n")
            builder.append("- Hostname: ${hostname ?: "None"}\n\n")
            
            // Permission status
            val hasPermission = hasDnsPermission(context)
            builder.append("Permission Status:\n")
            builder.append("- DNS Permission: ${if (hasPermission) "Granted" else "Denied"}\n\n")
            
            // Test write permission
            val writeTest = testDnsWritePermission(context)
            builder.append("Write Permission Test:\n")
            builder.append("- Success: ${writeTest.success}\n")
            if (!writeTest.success) {
                builder.append("- Error: ${writeTest.errorMessage}\n")
                builder.append("- Code: ${writeTest.errorCode}\n")
                builder.append("- Details: ${writeTest.details}\n")
            }
            builder.append("\n")
            
            // System information
            builder.append("System Information:\n")
            builder.append("- Android Version: ${android.os.Build.VERSION.RELEASE}\n")
            builder.append("- API Level: ${android.os.Build.VERSION.SDK_INT}\n")
            builder.append("- Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\n")
            
        } catch (e: Exception) {
            builder.append("Error gathering troubleshooting info: ${e.message}\n")
        }
        
        return builder.toString()
    }
}
