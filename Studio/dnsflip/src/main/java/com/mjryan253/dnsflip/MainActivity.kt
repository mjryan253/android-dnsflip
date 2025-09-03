package com.mjryan253.dnsflip

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.mjryan253.dnsflip.ui.components.LargeLightSwitch
import com.mjryan253.dnsflip.ui.components.OnboardingDialog
import com.mjryan253.dnsflip.ui.theme.*
import android.util.Log

/**
 * MainActivity - Primary entry point for DNSFlip application
 * 
 * This activity hosts the main UI for DNS switching functionality. It integrates
 * all core components including DNS management, permission handling, and user preferences.
 * 
 * Key Features:
 * - Beautiful dark theme with OLED optimization
 * - Large light switch for intuitive DNS toggling
 * - Real-time permission status monitoring
 * - Comprehensive onboarding for permission setup
 * - Automatic hostname persistence
 * 
 * @author DNSFlip Team
 * @version 1.0
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DNSFlipTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dnsManager = remember { DNSManager() }
    val preferencesManager = remember { PreferencesManager(context) }
    val shizukuManager = remember { ShizukuManager(context) }
    
    // State variables
    var dnsHostname by remember { mutableStateOf("1.1.1.1") }
    var hasPermission by remember { mutableStateOf(false) }
    var currentDnsMode by remember { mutableStateOf("unknown") }
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showErrorDetails by remember { mutableStateOf(false) }
    
    // Collect Shizuku state
    val shizukuState by shizukuManager.shizukuState.collectAsState()
    val hasShizukuPermission by shizukuManager.hasPermission.collectAsState()
    val lastError by shizukuManager.lastError.collectAsState()
    
    // Check permission and DNS status on startup
    LaunchedEffect(Unit) {
        Log.d("MainActivity", "App startup - checking permissions and DNS status")
        
        // First check Shizuku status and automatically request permissions if needed
        Log.d("MainActivity", "Auto-checking Shizuku permissions on startup")
        shizukuManager.autoCheckAndRequestPermissions()
        
        // Wait a moment for Shizuku status to update
        delay(1000)
        
        // Now check DNS permission and status
        hasPermission = dnsManager.hasDnsPermission(context)
        currentDnsMode = dnsManager.getCurrentDnsMode(context)
        
        // Load saved hostname from SharedPreferences
        dnsHostname = preferencesManager.loadDnsHostname()
        
        // If custom DNS is currently active, use the system hostname
        if (currentDnsMode == "custom") {
            dnsManager.getCurrentDnsHostname(context)?.let { hostname ->
                dnsHostname = hostname
            }
        }
        
        Log.d("MainActivity", "Initial state - hasPermission: $hasPermission, currentDnsMode: $currentDnsMode, dnsHostname: $dnsHostname")
        
        // Show onboarding if no permission
        if (!hasPermission) {
            showOnboardingDialog = true
        }
    }
    
    // Update permission status when Shizuku state changes
    LaunchedEffect(hasShizukuPermission, shizukuState) {
        Log.d("MainActivity", "Shizuku state changed - hasShizukuPermission: $hasShizukuPermission, shizukuState: $shizukuState")
        
        if (hasShizukuPermission && shizukuState == com.mjryan253.dnsflip.ShizukuState.READY) {
            Log.i("MainActivity", "Shizuku permission granted, checking DNS permission")
            hasPermission = dnsManager.hasDnsPermission(context)
            if (hasPermission) {
                showOnboardingDialog = false
                snackbarMessage = "Permission granted via Shizuku!"
                showSnackbar = true
                Log.i("MainActivity", "DNS permission confirmed via Shizuku")
            } else {
                Log.w("MainActivity", "Shizuku permission granted but DNS permission still denied")
                snackbarMessage = "Shizuku permission granted but DNS permission still denied"
                showSnackbar = true
            }
        } else if (shizukuState == com.mjryan253.dnsflip.ShizukuState.ERROR) {
            Log.w("MainActivity", "Shizuku error state detected")
            snackbarMessage = "Shizuku error: ${lastError ?: "Unknown error"}"
            showSnackbar = true
        } else if (shizukuState == com.mjryan253.dnsflip.ShizukuState.PERMISSION_REQUIRED) {
            Log.d("MainActivity", "Shizuku permission required")
            // Don't show snackbar here as it might be from a permission request
        }
    }
    
    // Periodically refresh Shizuku status to catch changes
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Check every 5 seconds
            if (shizukuState != com.mjryan253.dnsflip.ShizukuState.READY) {
                Log.d("MainActivity", "Periodic Shizuku status check")
                shizukuManager.checkShizukuStatus()
            }
        }
    }
    
    // Determine if custom DNS is active
    val isCustomDnsActive = currentDnsMode == "custom"
    
    // Debounced save of hostname when user types
    LaunchedEffect(dnsHostname) {
        if (dnsHostname.isNotBlank() && dnsHostname != "1.1.1.1") {
            delay(1000) // Wait 1 second after user stops typing
            preferencesManager.saveDnsHostname(dnsHostname)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // App Title
        Text(
            text = "DNSFlip",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        // Shizuku Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Shizuku Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Text(
                    text = shizukuManager.getStatusMessage(),
                    fontSize = 16.sp,
                    color = when (shizukuState) {
                        com.mjryan253.dnsflip.ShizukuState.READY -> StatusSuccess
                        com.mjryan253.dnsflip.ShizukuState.ERROR -> StatusError
                        else -> TextSecondary
                    },
                    textAlign = TextAlign.Center
                )
                Text(
                    text = shizukuManager.getRecommendedAction(),
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                // Error details button
                if (lastError != null) {
                    Button(
                        onClick = { showErrorDetails = !showErrorDetails },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusError.copy(alpha = 0.8f))
                    ) {
                        Text(if (showErrorDetails) "Hide Error Details" else "Show Error Details")
                    }
                    
                    if (showErrorDetails) {
                        Text(
                            text = lastError!!,
                            fontSize = 12.sp,
                            color = StatusError,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
                
                // Shizuku action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { shizukuManager.checkShizukuStatus() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SwitchTrack)
                    ) {
                        Text("Refresh Status")
                    }
                    
                    when (shizukuState) {
                        com.mjryan253.dnsflip.ShizukuState.NOT_INSTALLED -> {
                            Button(
                                onClick = { shizukuManager.openShizukuDownload() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                            ) {
                                Text("Install Shizuku")
                            }
                        }
                        com.mjryan253.dnsflip.ShizukuState.NOT_RUNNING -> {
                            Button(
                                onClick = { shizukuManager.openShizukuApp() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                            ) {
                                Text("Start Shizuku")
                            }
                        }
                        com.mjryan253.dnsflip.ShizukuState.PERMISSION_REQUIRED -> {
                            Button(
                                onClick = { shizukuManager.requestPermission() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                            ) {
                                Text("Request Permission")
                            }
                        }
                        else -> {
                            // No additional action needed
                        }
                    }
                }
            }
        }
        
        // DNS Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Current DNS Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Text(
                    text = dnsManager.getDnsStatusDescription(context),
                    fontSize = 16.sp,
                    color = if (hasPermission) TextPrimary else StatusError,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Large Light Switch
        LargeLightSwitch(
            isOn = isCustomDnsActive,
            onToggle = {
                if (!hasPermission) {
                    showOnboardingDialog = true
                } else {
                    val success = if (isCustomDnsActive) {
                        dnsManager.setDnsModeOff(context)
                    } else {
                        dnsManager.setDnsModeOn(context, dnsHostname)
                    }
                    
                    if (success) {
                        currentDnsMode = dnsManager.getCurrentDnsMode(context)
                        
                        // Save hostname to SharedPreferences on successful DNS change
                        if (!isCustomDnsActive) {
                            preferencesManager.saveDnsHostname(dnsHostname)
                            preferencesManager.saveLastDnsMode("custom")
                        } else {
                            preferencesManager.saveLastDnsMode("automatic")
                        }
                        
                        snackbarMessage = if (isCustomDnsActive) "Switched to System DNS" else "Switched to Custom DNS"
                        showSnackbar = true
                    } else {
                        snackbarMessage = "Failed to change DNS settings"
                        showSnackbar = true
                    }
                }
            },
            enabled = hasPermission
        )
        
        // DNS Hostname Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "DNS Server",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                OutlinedTextField(
                    value = dnsHostname,
                    onValueChange = { dnsHostname = it },
                    label = { Text("Enter DNS hostname (e.g., 1.1.1.1, dns.google)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = hasPermission,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwitchOn,
                        unfocusedBorderColor = SwitchTrack,
                        focusedLabelColor = SwitchOn,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Text(
                    text = "Popular options: 1.1.1.1 (Cloudflare), 8.8.8.8 (Google), 9.9.9.9 (Quad9)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Permission Check Button
        Button(
            onClick = {
                hasPermission = dnsManager.hasDnsPermission(context)
                currentDnsMode = dnsManager.getCurrentDnsMode(context)
                if (hasPermission) {
                    snackbarMessage = "Permission granted!"
                    showSnackbar = true
                } else {
                    showOnboardingDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasPermission) StatusSuccess else StatusWarning
            )
        ) {
            Text(
                text = if (hasPermission) "Permission Granted ✓" else "Setup Permission",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Permission Instructions (shown when permission is not granted)
        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Permission Required",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusError
                    )
                    Text(
                        text = "This app needs WRITE_SECURE_SETTINGS permission to change DNS settings. Grant permission using ADB:",
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "ADB Command",
                                "adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "ADB command copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                    ) {
                        Text("Copy ADB Command")
                    }
                }
            }
        }
    }
    
    // Onboarding Dialog
    OnboardingDialog(
        isVisible = showOnboardingDialog,
        onDismiss = { showOnboardingDialog = false },
        shizukuManager = shizukuManager,
        onPermissionGranted = {
            hasPermission = dnsManager.hasDnsPermission(context)
            showOnboardingDialog = false
            snackbarMessage = "Permission granted! You can now use DNSFlip."
            showSnackbar = true
        }
    )
    
    // Snackbar for feedback
    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            delay(3000)
            showSnackbar = false
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                containerColor = StatusSuccess,
                contentColor = TextPrimary
            ) {
                Text(snackbarMessage)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DNSFlipTheme {
        MainScreen()
    }
}