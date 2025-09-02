package com.mjryan253.dnsflip

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import dev.rikka.shizuku.api.Shizuku
import dev.rikka.shizuku.api.ShizukuBinderWrapper
import dev.rikka.shizuku.api.ShizukuProvider
import dev.rikka.shizuku.api.ShizukuUserServiceArgs
import dev.rikka.shizuku.api.ShizukuUserServiceArgs.Companion.USER_SERVICE_ARG_COMPONENT
import dev.rikka.shizuku.api.ShizukuUserServiceArgs.Companion.USER_SERVICE_ARG_PROCESS_NAME_MEDIUM
import dev.rikka.shizuku.api.ShizukuUserServiceArgs.Companion.USER_SERVICE_ARG_VERSION
import dev.rikka.shizuku.api.ShizukuUserServiceArgs.Companion.USER_SERVICE_ARG_VERSION_NAME
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ShizukuManager handles Shizuku integration for system permission access
 * This class manages Shizuku service detection, permission requests, and fallback to ADB
 */
class ShizukuManager(private val context: Context) {
    
    companion object {
        private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        private const val SHIZUKU_ACTIVITY = "moe.shizuku.privileged.api.MainActivity"
    }
    
    private val _shizukuState = MutableStateFlow(ShizukuState.UNKNOWN)
    val shizukuState: StateFlow<ShizukuState> = _shizukuState.asStateFlow()
    
    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    private val shizukuListener = object : Shizuku.OnBinderReceivedListener {
        override fun onBinderReceived() {
            checkShizukuStatus()
        }
    }
    
    private val shizukuRequestPermissionResultListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            _hasPermission.value = grantResult == Shizuku.PERMISSION_GRANTED
            _shizukuState.value = if (_hasPermission.value) {
                ShizukuState.READY
            } else {
                ShizukuState.PERMISSION_DENIED
            }
        }
    }
    
    init {
        // Register Shizuku listeners
        Shizuku.addBinderReceivedListener(shizukuListener)
        Shizuku.addRequestPermissionResultListener(shizukuRequestPermissionResultListener)
        
        // Check initial status
        checkShizukuStatus()
    }
    
    /**
     * Checks the current Shizuku status and permission state
     */
    fun checkShizukuStatus() {
        when {
            !isShizukuInstalled() -> {
                _shizukuState.value = ShizukuState.NOT_INSTALLED
                _hasPermission.value = false
            }
            !Shizuku.pingBinder() -> {
                _shizukuState.value = ShizukuState.NOT_RUNNING
                _hasPermission.value = false
            }
            Shizuku.checkSelfPermission() == Shizuku.PERMISSION_GRANTED -> {
                _shizukuState.value = ShizukuState.READY
                _hasPermission.value = true
            }
            Shizuku.checkSelfPermission() == Shizuku.PERMISSION_DENIED -> {
                _shizukuState.value = ShizukuState.PERMISSION_DENIED
                _hasPermission.value = false
            }
            else -> {
                _shizukuState.value = ShizukuState.PERMISSION_REQUIRED
                _hasPermission.value = false
            }
        }
    }
    
    /**
     * Requests permission through Shizuku
     */
    fun requestPermission() {
        if (Shizuku.checkSelfPermission() == Shizuku.PERMISSION_GRANTED) {
            _hasPermission.value = true
            _shizukuState.value = ShizukuState.READY
            return
        }
        
        if (Shizuku.checkSelfPermission() == Shizuku.PERMISSION_DENIED) {
            _shizukuState.value = ShizukuState.PERMISSION_DENIED
            return
        }
        
        try {
            Shizuku.requestPermission(0)
        } catch (e: Exception) {
            _shizukuState.value = ShizukuState.ERROR
        }
    }
    
    /**
     * Checks if Shizuku is installed on the device
     */
    fun isShizukuInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Opens Shizuku app for installation or setup
     */
    fun openShizukuApp() {
        try {
            val intent = Intent().apply {
                setClassName(SHIZUKU_PACKAGE, SHIZUKU_ACTIVITY)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to Play Store or GitHub
            openShizukuDownload()
        }
    }
    
    /**
     * Opens Shizuku download page
     */
    fun openShizukuDownload() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RikkaApps/Shizuku/releases"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Please install Shizuku from GitHub releases", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Gets user-friendly status message
     */
    fun getStatusMessage(): String {
        return when (_shizukuState.value) {
            ShizukuState.READY -> "Shizuku is ready and permission granted"
            ShizukuState.NOT_INSTALLED -> "Shizuku is not installed"
            ShizukuState.NOT_RUNNING -> "Shizuku is installed but not running"
            ShizukuState.PERMISSION_REQUIRED -> "Shizuku permission required"
            ShizukuState.PERMISSION_DENIED -> "Shizuku permission denied"
            ShizukuState.ERROR -> "Shizuku error occurred"
            ShizukuState.UNKNOWN -> "Checking Shizuku status..."
        }
    }
    
    /**
     * Gets the recommended action for the current state
     */
    fun getRecommendedAction(): String {
        return when (_shizukuState.value) {
            ShizukuState.READY -> "Ready to use"
            ShizukuState.NOT_INSTALLED -> "Install Shizuku"
            ShizukuState.NOT_RUNNING -> "Start Shizuku service"
            ShizukuState.PERMISSION_REQUIRED -> "Grant permission"
            ShizukuState.PERMISSION_DENIED -> "Use ADB method"
            ShizukuState.ERROR -> "Try ADB method"
            ShizukuState.UNKNOWN -> "Please wait..."
        }
    }
    
    /**
     * Cleans up listeners
     */
    fun cleanup() {
        Shizuku.removeBinderReceivedListener(shizukuListener)
        Shizuku.removeRequestPermissionResultListener(shizukuRequestPermissionResultListener)
    }
}

/**
 * Represents the current state of Shizuku integration
 */
enum class ShizukuState {
    UNKNOWN,
    NOT_INSTALLED,
    NOT_RUNNING,
    PERMISSION_REQUIRED,
    PERMISSION_DENIED,
    READY,
    ERROR
}
