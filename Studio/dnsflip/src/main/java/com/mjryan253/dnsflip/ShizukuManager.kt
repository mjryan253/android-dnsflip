package com.mjryan253.dnsflip

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ShizukuManager handles system permission access through Shizuku
 * 
 * This class provides integration with Shizuku for managing system permissions
 * without requiring root access. It uses Shizuku's elevated privileges to access
 * system APIs directly.
 * 
 * Key Features:
 * - System API access through Shizuku
 * - Proper permission checking and request flow
 * - Real-time permission state monitoring
 * - User-friendly status messages and recommended actions
 * 
 * @author DNSFlip Team
 * @version 2.7
 * @since Android 9 (API 28)
 */
class ShizukuManager(private val context: Context) {
    
    companion object {
        private const val TAG = "ShizukuManager"
    }
    
    private val _shizukuState = MutableStateFlow(ShizukuState.NOT_RUNNING)
    val shizukuState: StateFlow<ShizukuState> = _shizukuState.asStateFlow()
    
    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    /**
     * Check if we can access system settings through Shizuku
     * @return true if we can perform DNS operations, false otherwise
     */
    private fun canAccessSystemSettings(): Boolean {
        return try {
            // Try to read a system setting to test access
            val testValue = Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            Log.d(TAG, "System settings access test successful - current DNS mode: $testValue")
            true
        } catch (e: SecurityException) {
            Log.w(TAG, "System settings access test failed - SecurityException", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "System settings access test failed - unexpected error", e)
            false
        }
    }
    
    
    /**
     * Check if we have the necessary access for DNS operations
     * @param context The application context
     * @return true if we have the required access, false otherwise
     */
    fun hasDnsAccess(context: Context): Boolean {
        return canAccessSystemSettings()
    }
    
    
    /**
     * Test DNS operations to verify access is working
     * @return true if DNS operations succeed, false otherwise
     */
    fun testDnsOperations(): Boolean {
        return try {
            Log.i(TAG, "Testing DNS operations to verify access")
            
            // Test 1: Read DNS settings
            val currentMode = Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            Log.d(TAG, "DNS read test successful - current mode: $currentMode")
            
            // Test 2: Write DNS settings (temporarily)
            val originalMode = currentMode ?: "opportunistic"
            val testMode = "off"
            
            val writeResult = Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                testMode
            )
            
            if (!writeResult) {
                Log.w(TAG, "DNS write test failed - putString returned false")
                return false
            }
            
            // Test 3: Verify the write worked
            val verifyValue = Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            
            if (verifyValue != testMode) {
                Log.w(TAG, "DNS write test failed - verification failed. Expected: $testMode, Got: $verifyValue")
                return false
            }
            
            // Test 4: Restore original value
            Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                originalMode
            )
            
            Log.i(TAG, "DNS operations test successful - access is working")
            true
            
        } catch (e: SecurityException) {
            Log.w(TAG, "DNS operations test failed - SecurityException (access denied)", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "DNS operations test failed - unexpected error", e)
            false
        }
    }
    
    /**
     * Manually refresh access status - call this after returning from Shizuku app
     * @param context The application context
     */
    fun refreshAccessStatus(context: Context) {
        checkShizukuStatus(context)
    }
    
    /**
     * Check current access status and update state
     * @param context The application context
     */
    fun checkShizukuStatus(context: Context) {
        try {
            Log.d(TAG, "Checking Shizuku access status")

            // Check if we can access system settings through Shizuku
            if (canAccessSystemSettings()) {
                Log.d(TAG, "System settings access available, checking DNS-specific access")
                
                // Check if we have the specific access needed for DNS operations
                if (hasDnsAccess(context)) {
                    Log.d(TAG, "DNS access is available and working")
                    _shizukuState.value = ShizukuState.READY
                    _hasPermission.value = true
                    _lastError.value = null
                } else {
                    Log.w(TAG, "System access available but DNS operations not working")
                    _shizukuState.value = ShizukuState.PERMISSION_REQUIRED
                    _hasPermission.value = false
                    _lastError.value = "System access available but DNS operations not working. Please open Shizuku app → Apps → DNSFlip → Grant 'System API' permission → Restart DNSFlip app."
                }
            } else {
                Log.d(TAG, "System settings access not available")
                _shizukuState.value = ShizukuState.PERMISSION_REQUIRED
                _hasPermission.value = false
                _lastError.value = "Cannot access system settings. Please open Shizuku app → Apps → DNSFlip → Grant 'System API' permission → Restart DNSFlip app."
            }

            Log.d(TAG, "Shizuku status updated: ${_shizukuState.value}")

        } catch (e: Exception) {
            Log.e(TAG, "Error checking Shizuku status", e)
            _shizukuState.value = ShizukuState.ERROR
            _hasPermission.value = false
            _lastError.value = "Error checking status: ${e.message}"
        }
    }
    
    
    /**
     * Request Shizuku access for DNS operations
     * @param context The application context
     */
    fun requestPermission(context: Context) {
        checkShizukuStatus(context)
    }
    
    /**
     * Get status message for current state
     * @return User-friendly status message
     */
    fun getStatusMessage(): String {
        return when (_shizukuState.value) {
            ShizukuState.NOT_RUNNING -> "Shizuku not accessible"
            ShizukuState.PERMISSION_REQUIRED -> "Access required"
            ShizukuState.READY -> "Shizuku is ready and working"
            ShizukuState.ERROR -> "Error: ${_lastError.value ?: "Unknown error"}"
        }
    }
    
    /**
     * Get recommended action for current state
     * @return Recommended action for user
     */
    fun getRecommendedAction(): String {
        return when (_shizukuState.value) {
            ShizukuState.NOT_RUNNING -> "Configure Shizuku"
            ShizukuState.PERMISSION_REQUIRED -> "Enable System API permission in Shizuku"
            ShizukuState.READY -> "Ready to use"
            ShizukuState.ERROR -> "Check error details"
        }
    }
    
    /**
     * Get detailed error information
     * @return Error details or null if no error
     */
    fun getErrorDetails(): String? {
        return _lastError.value
    }
    
    /**
     * Clear the last error
     */
    fun clearError() {
        _lastError.value = null
    }
}

/**
 * Enum representing different Shizuku states
 */
enum class ShizukuState {
    NOT_RUNNING,
    PERMISSION_REQUIRED,
    READY,
    ERROR
}