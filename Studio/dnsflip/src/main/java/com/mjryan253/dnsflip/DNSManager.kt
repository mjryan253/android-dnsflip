package com.mjryan253.dnsflip

import android.content.Context
import android.provider.Settings

/**
 * DNSManager - Core DNS switching functionality for DNSFlip
 * 
 * This class provides a clean interface for managing Android's private DNS settings
 * through the Settings.Global API. It handles the complexity of different DNS modes
 * and provides user-friendly status descriptions.
 * 
 * Key Features:
 * - Switch between automatic (system) and custom DNS
 * - Read current DNS configuration
 * - Validate permissions before operations
 * - Provide human-readable status descriptions
 * 
 * @author DNSFlip Team
 * @version 1.0
 * @since Android 9 (API 28)
 */
class DNSManager {
    
    companion object {
        // DNS mode constants
        private const val DNS_MODE_AUTOMATIC = "automatic"
        private const val DNS_MODE_CUSTOM = "custom"
        
        // Settings.Global keys for DNS configuration
        private const val PRIVATE_DNS_MODE = Settings.Global.PRIVATE_DNS_MODE
        private const val PRIVATE_DNS_SPECIFIER = Settings.Global.PRIVATE_DNS_SPECIFIER
        
        // DNS mode values
        private const val PRIVATE_DNS_MODE_OFF = "off"
        private const val PRIVATE_DNS_MODE_OPPORTUNISTIC = "opportunistic"
        private const val PRIVATE_DNS_MODE_STRICT = "strict"
    }
    
    /**
     * Gets the current DNS mode from system settings
     * @param context Application context
     * @return Current DNS mode as a string ("automatic", "custom", or "off")
     */
    fun getCurrentDnsMode(context: Context): String {
        return try {
            val dnsMode = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            val dnsSpecifier = Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
            
            when (dnsMode) {
                PRIVATE_DNS_MODE_OFF -> "off"
                PRIVATE_DNS_MODE_OPPORTUNISTIC -> "automatic"
                PRIVATE_DNS_MODE_STRICT -> if (dnsSpecifier.isNullOrEmpty()) "automatic" else "custom"
                else -> "automatic"
            }
        } catch (e: SecurityException) {
            // Permission not granted
            "unknown"
        } catch (e: Exception) {
            // Other error
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
            Settings.Global.getString(context.contentResolver, PRIVATE_DNS_SPECIFIER)
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
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
            Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_OPPORTUNISTIC)
            Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, "")
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
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
            return false
        }
        
        return try {
            Settings.Global.putString(context.contentResolver, PRIVATE_DNS_MODE, PRIVATE_DNS_MODE_STRICT)
            Settings.Global.putString(context.contentResolver, PRIVATE_DNS_SPECIFIER, hostname.trim())
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
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
            // Try to read the current DNS mode - if this succeeds, we have permission
            Settings.Global.getString(context.contentResolver, PRIVATE_DNS_MODE)
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
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
        
        return when (mode) {
            "off" -> "Status: Off (No DNS protection)"
            "automatic" -> "Status: Automatic (System DNS)"
            "custom" -> "Status: Custom (${hostname ?: "Unknown"})"
            "unknown" -> "Status: Unknown (Permission required)"
            "error" -> "Status: Error (Check permissions)"
            else -> "Status: Unknown"
        }
    }
}
