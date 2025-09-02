package com.mjryan253.dnsflip

import android.content.Context
import android.content.SharedPreferences

/**
 * PreferencesManager handles saving and loading app preferences using SharedPreferences
 * This class manages the persistence of DNS hostname and other app settings
 */
class PreferencesManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "dnsflip_preferences"
        private const val KEY_DNS_HOSTNAME = "dns_hostname"
        private const val KEY_LAST_DNS_MODE = "last_dns_mode"
        private const val DEFAULT_HOSTNAME = "1.1.1.1"
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Saves the DNS hostname to SharedPreferences
     * @param hostname The DNS hostname to save
     */
    fun saveDnsHostname(hostname: String) {
        if (hostname.isNotBlank()) {
            sharedPreferences.edit()
                .putString(KEY_DNS_HOSTNAME, hostname.trim())
                .apply()
        }
    }
    
    /**
     * Loads the saved DNS hostname from SharedPreferences
     * @return The saved hostname or default if none exists
     */
    fun loadDnsHostname(): String {
        return sharedPreferences.getString(KEY_DNS_HOSTNAME, DEFAULT_HOSTNAME) ?: DEFAULT_HOSTNAME
    }
    
    /**
     * Saves the last DNS mode that was set
     * @param mode The DNS mode ("custom", "automatic", "off")
     */
    fun saveLastDnsMode(mode: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_DNS_MODE, mode)
            .apply()
    }
    
    /**
     * Loads the last DNS mode that was set
     * @return The last DNS mode or "automatic" if none exists
     */
    fun loadLastDnsMode(): String {
        return sharedPreferences.getString(KEY_LAST_DNS_MODE, "automatic") ?: "automatic"
    }
    
    /**
     * Clears all saved preferences
     */
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Checks if there are any saved preferences
     * @return true if preferences exist, false otherwise
     */
    fun hasPreferences(): Boolean {
        return sharedPreferences.contains(KEY_DNS_HOSTNAME) || 
               sharedPreferences.contains(KEY_LAST_DNS_MODE)
    }
    
    /**
     * Gets all saved preferences as a map for debugging
     * @return Map of all saved preferences
     */
    fun getAllPreferences(): Map<String, Any?> {
        return sharedPreferences.all
    }
}
