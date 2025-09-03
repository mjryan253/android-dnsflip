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
 * 
 * @author DNSFlip Team
 * @version 1.0
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
     * Sets DNS mode to automatic (uses system default DNS)
     * @param context Application context
     * @return true if successful, false otherwise
     */
    fun setDnsModeOff(context: Context): Boolean {
        return try {
            Log.d(TAG, "Setting DNS mode to automatic (off)")
            
            // Set mode to opportunistic (automatic)
            val modeResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_OPPORTUNISTIC)
            if (!modeResult) {
                Log.e(TAG, "Failed to set DNS mode to opportunistic")
                return false
            }
            
            // Clear the specifier
            val specifierResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, "")
            if (!specifierResult) {
                Log.e(TAG, "Failed to clear DNS specifier")
                return false
            }
            
            Log.i(TAG, "Successfully set DNS mode to automatic")
            true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when setting DNS mode to automatic - permission denied", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when setting DNS mode to automatic", e)
            false
        }
    }
    
    /**
     * Sets DNS mode to use a custom DNS hostname
     * @param context Application context
     * @param hostname The DNS hostname to use (e.g., "1.1.1.1", "dns.google")
     * @return true if successful, false otherwise
     */
    fun setDnsModeOn(context: Context, hostname: String): Boolean {
        if (hostname.isBlank()) {
            Log.w(TAG, "Cannot set DNS mode: hostname is blank")
            return false
        }
        
        val trimmedHostname = hostname.trim()
        Log.d(TAG, "Setting DNS mode to custom with hostname: $trimmedHostname")
        
        return try {
            // Set mode to strict (custom)
            val modeResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_STRICT)
            if (!modeResult) {
                Log.e(TAG, "Failed to set DNS mode to strict")
                return false
            }
            
            // Set the specifier
            val specifierResult = Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, trimmedHostname)
            if (!specifierResult) {
                Log.e(TAG, "Failed to set DNS specifier to: $trimmedHostname")
                return false
            }
            
            Log.i(TAG, "Successfully set DNS mode to custom with hostname: $trimmedHostname")
            true
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException when setting DNS mode to custom - permission denied", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when setting DNS mode to custom", e)
            false
        }
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
     * Validates a DNS hostname for proper format
     * @param hostname The hostname to validate
     * @return true if valid, false otherwise
     */
    fun isValidHostname(hostname: String): Boolean {
        if (hostname.isBlank()) {
            Log.d(TAG, "Hostname validation failed: blank")
            return false
        }
        
        val trimmed = hostname.trim()
        
        // Basic validation - should be non-empty and not too long
        if (trimmed.length > 253) {
            Log.d(TAG, "Hostname validation failed: too long (${trimmed.length} chars)")
            return false
        }
        
        // Check for valid characters (basic validation)
        val validPattern = Regex("^[a-zA-Z0-9.-]+$")
        val isValid = validPattern.matches(trimmed)
        
        Log.d(TAG, "Hostname validation result: $isValid for '$trimmed'")
        return isValid
    }
    
    /**
     * Gets detailed error information for debugging
     * @param context Application context
     * @return Detailed error information or null if no error
     */
    fun getDetailedErrorInfo(context: Context): String? {
        return try {
            // Try to get DNS mode to see what happens
            val dnsMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            null // No error if we can read
        } catch (e: SecurityException) {
            "Permission denied: ${e.message}"
        } catch (e: Exception) {
            "Unexpected error: ${e.message}"
        }
    }
}
