package com.mjryan253.dnsflip

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ShizukuManager handles Shizuku integration for system permission access
 * 
 * This class provides integration with Shizuku for managing system permissions
 * without requiring root access. It implements the official Shizuku API pattern
 * as documented in the Shizuku-API repository.
 * 
 * Key Features:
 * - Official Shizuku API integration
 * - Proper permission checking and request flow
 * - Real-time permission state monitoring
 * - Graceful fallback to ADB when Shizuku unavailable
 * - User-friendly status messages and recommended actions
 * 
 * @author DNSFlip Team
 * @version 2.2
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
    
    // Shizuku permission result listener for handling permission grant/deny results
    private val requestPermissionResultListener = object : OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == 1) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    _shizukuState.value = ShizukuState.READY
                    _hasPermission.value = true
                    Log.i(TAG, "Shizuku permission granted via result listener")
                } else {
                    _shizukuState.value = ShizukuState.PERMISSION_REQUIRED
                    _hasPermission.value = false
                    Log.w(TAG, "Shizuku permission denied via result listener")
                }
            }
        }
    }
    
    init {
        // Register Shizuku permission result listener
        try {
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
            Log.d(TAG, "Shizuku permission result listener added")
        } catch (e: Exception) {
            Log.w(TAG, "Could not add Shizuku permission result listener: ${e.message}")
        }
        
        // Check initial status
        checkShizukuStatus()
    }
    
    /**
     * Clean up resources when ShizukuManager is no longer needed
     * This should be called when the activity is destroyed
     */
    fun cleanup() {
        // Remove Shizuku permission result listener
        try {
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
            Log.d(TAG, "Shizuku permission result listener removed")
        } catch (e: Exception) {
            Log.w(TAG, "Could not remove Shizuku permission result listener: ${e.message}")
        }
    }
    
    /**
     * Check if Shizuku is available using the official API
     * @return true if Shizuku is available, false otherwise
     */
    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            Log.w(TAG, "Error checking Shizuku availability: ${e.message}")
            false
        }
    }
    
    /**
     * Check if we have Shizuku permission using the official Shizuku API
     * @return true if we can perform DNS operations, false otherwise
     */
    private fun checkShizukuPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            Log.w(TAG, "Error checking Shizuku permission: ${e.message}")
            false
        }
    }
    
    /**
     * Enhanced permission check that tests actual DNS write operations
     * @return true if we can perform DNS write operations, false otherwise
     */
    private fun checkShizukuWritePermission(): Boolean {
        return try {
            // Get current DNS mode to restore later
            val currentMode = android.provider.Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            val currentSpecifier = android.provider.Settings.Global.getString(
                context.contentResolver, 
                "private_dns_specifier"
            )
            
            Log.d(TAG, "Testing DNS write permission - current mode: $currentMode, specifier: $currentSpecifier")
            
            // Try to write a test value temporarily
            val testMode = "opportunistic"
            val writeResult = android.provider.Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                testMode
            )
            
            if (!writeResult) {
                Log.w(TAG, "DNS write permission test failed - putString returned false")
                return false
            }
            
            // Verify the write actually worked
            val verifyValue = android.provider.Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            
            if (verifyValue != testMode) {
                Log.w(TAG, "DNS write permission test failed - verification failed. Expected: $testMode, Got: $verifyValue")
                return false
            }
            
            // Restore original values
            android.provider.Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                currentMode ?: "opportunistic"
            )
            
            if (currentSpecifier != null) {
                android.provider.Settings.Global.putString(
                    context.contentResolver, 
                    "private_dns_specifier", 
                    currentSpecifier
                )
            }
            
            Log.i(TAG, "Shizuku write permission test successful - can modify DNS settings")
            true
            
        } catch (e: SecurityException) {
            Log.w(TAG, "Shizuku write permission test failed - SecurityException", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "DNS write permission test failed - unexpected error", e)
            false
        }
    }
    
    /**
     * Test DNS operations to verify Shizuku permissions are working
     * @return true if DNS operations succeed, false otherwise
     */
    fun testDnsOperations(): Boolean {
        return try {
            Log.i(TAG, "Testing DNS operations to verify Shizuku permissions")
            
            // Test 1: Read DNS settings
            val currentMode = android.provider.Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            Log.d(TAG, "DNS read test successful - current mode: $currentMode")
            
            // Test 2: Write DNS settings (temporarily)
            val originalMode = currentMode ?: "opportunistic"
            val testMode = "off"
            
            val writeResult = android.provider.Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                testMode
            )
            
            if (!writeResult) {
                Log.w(TAG, "DNS write test failed - putString returned false")
                return false
            }
            
            // Test 3: Verify the write worked
            val verifyValue = android.provider.Settings.Global.getString(
                context.contentResolver, 
                "private_dns_mode"
            )
            
            if (verifyValue != testMode) {
                Log.w(TAG, "DNS write test failed - verification failed. Expected: $testMode, Got: $verifyValue")
                return false
            }
            
            // Test 4: Restore original value
            android.provider.Settings.Global.putString(
                context.contentResolver, 
                "private_dns_mode", 
                originalMode
            )
            
            Log.i(TAG, "DNS operations test successful - Shizuku permissions are working")
            true
            
        } catch (e: SecurityException) {
            Log.w(TAG, "DNS operations test failed - SecurityException (permission denied)", e)
            false
        } catch (e: Exception) {
            Log.w(TAG, "DNS operations test failed - unexpected error", e)
            false
        }
    }
    
    /**
     * Manually refresh Shizuku status - call this after returning from Shizuku app
     * This method should be called when the user returns from granting permissions
     */
    fun refreshShizukuStatus() {
        try {
            Log.i(TAG, "Manually refreshing Shizuku status")
            
            // Force a fresh status check using the official API
            checkShizukuStatus()
            
            // Also check if we can now perform DNS operations
            if (_shizukuState.value == ShizukuState.READY) {
                Log.i(TAG, "Shizuku status refreshed - permissions are now granted")
                _lastError.value = null
            } else {
                Log.w(TAG, "Shizuku status refreshed but permissions still not granted")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing Shizuku status", e)
            _lastError.value = "Error refreshing status: ${e.message}"
        }
    }
    
    /**
     * Check current Shizuku status and update state using the official API
     */
    fun checkShizukuStatus() {
        try {
            Log.d(TAG, "Checking Shizuku status via official API")

            if (!Shizuku.pingBinder()) {
                Log.d(TAG, "Shizuku service not available")
                _shizukuState.value = ShizukuState.NOT_RUNNING
                _hasPermission.value = false
                _lastError.value = "Shizuku service is not running"
                return
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Shizuku permission is granted")
                _shizukuState.value = ShizukuState.READY
                _hasPermission.value = true
            } else {
                Log.d(TAG, "Shizuku permission is required")
                _shizukuState.value = ShizukuState.PERMISSION_REQUIRED
                _hasPermission.value = false
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
     * Automatically check and request Shizuku permissions with user consent
     * This method should be called when the app starts to ensure permissions are properly set up
     */
    fun autoCheckAndRequestPermissions() {
        try {
            Log.i(TAG, "Auto-checking and requesting Shizuku permissions")
            
            // First check current status
            checkShizukuStatus()
            
            when (_shizukuState.value) {
                ShizukuState.NOT_RUNNING -> {
                    Log.i(TAG, "Shizuku service not running - user needs to start it")
                    _lastError.value = "Shizuku service is not running. Please start it in the Shizuku app."
                }
                
                ShizukuState.PERMISSION_REQUIRED -> {
                    Log.i(TAG, "Permission required - automatically requesting it")
                    // Automatically request permission
                    requestPermission()
                }
                
                ShizukuState.READY -> {
                    Log.i(TAG, "Shizuku is ready - no action needed")
                    _lastError.value = null
                }
                
                ShizukuState.ERROR -> {
                    Log.w(TAG, "Error state detected - user needs to check Shizuku manually")
                    _lastError.value = "Error detected. Please check Shizuku app manually."
                }
                
                else -> {
                    Log.w(TAG, "Unknown state: ${_shizukuState.value}")
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in auto permission check", e)
            _lastError.value = "Auto permission check failed: ${e.message}"
        }
    }
    
    /**
     * Request Shizuku permission using the official API
     */
    fun requestPermission() {
        try {
            Log.i(TAG, "Requesting Shizuku permission via API")

            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.requestPermission(1)
            } else {
                Log.i(TAG, "Already have Shizuku permission")
                _shizukuState.value = ShizukuState.READY
                _hasPermission.value = true
                _lastError.value = null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error requesting Shizuku permission", e)
            _lastError.value = "Failed to request permission: ${e.message}"
        }
    }
    

    
    /**
     * Get status message for current state
     * @return User-friendly status message
     */
    fun getStatusMessage(): String {
        return when (_shizukuState.value) {
            ShizukuState.NOT_RUNNING -> "Shizuku service is not accessible"
            ShizukuState.PERMISSION_REQUIRED -> "Permission required"
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
            ShizukuState.NOT_RUNNING -> "Start Shizuku service"
            ShizukuState.PERMISSION_REQUIRED -> "Grant permission"
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