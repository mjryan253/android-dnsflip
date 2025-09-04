package com.mjryan253.dnsflip

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.mjryan253.dnsflip.ui.theme.*
import android.util.Log

class MainActivity : ComponentActivity() {

    private lateinit var vpnManager: VpnManager
    private var dnsHostnameState by mutableStateOf("1.1.1.1")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vpnManager = VpnManager()
        enableEdgeToEdge()
        setContent {
            DNSFlipTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        vpnManager = vpnManager,
                        onHostnameChange = { dnsHostnameState = it }
                    )
                }
            }
        }
    }

    @Deprecated("This method is deprecated but required for VpnService.prepare")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VpnManager.VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            vpnManager.handleVpnPermissionResult(this, dnsHostnameState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    vpnManager: VpnManager,
    onHostnameChange: (String) -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    
    // State variables
    var dnsHostname by remember { mutableStateOf("1.1.1.1") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var lastDnsError by remember { mutableStateOf<String?>(null) }
    var isCustomDnsActive by remember { mutableStateOf(vpnManager.isVpnActive()) }

    // Initialize state and periodic check
    LaunchedEffect(Unit) {
        Log.d("MainActivity", "App startup - checking DNS status")
        dnsHostname = preferencesManager.loadDnsHostname()
        onHostnameChange(dnsHostname) // Update activity-level state

        // Periodically check VPN status
        while (true) {
            val vpnIsRunning = vpnManager.isVpnActive()
            if (isCustomDnsActive != vpnIsRunning) {
                isCustomDnsActive = vpnIsRunning
                if (vpnIsRunning) {
                    vpnManager.getCurrentDns()?.let {
                        dnsHostname = it
                    }
                }
            }
            delay(1000) // Check every second
        }
    }
    
    // Debounced save of hostname when user types
    LaunchedEffect(dnsHostname) {
        if (dnsHostname.isNotBlank() && dnsHostname != "1.1.1.1") {
            onHostnameChange(dnsHostname) // Update activity-level state for onActivityResult
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
        
        
        // DNS Status Card
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
                
                val statusText = if (isCustomDnsActive) {
                    "Active: ${vpnManager.getCurrentDns()}"
                } else {
                    "Inactive"
                }
                Text(
                    text = statusText,
                    fontSize = 16.sp,
                    color = if (isCustomDnsActive) StatusSuccess else TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Last updated timestamp
                Text(
                    text = "Last updated: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Large Light Switch
        LargeLightSwitch(
            isOn = isCustomDnsActive,
            onToggle = {
                if (isCustomDnsActive) {
                    vpnManager.stopVpn(context)
                    snackbarMessage = "Switched to System DNS"
                } else {
                    vpnManager.startVpn(context, dnsHostname)
                    snackbarMessage = "Switching to Custom DNS..."
                }
                showSnackbar = true
            },
            enabled = dnsHostname.isNotBlank()
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
                    enabled = !isCustomDnsActive,
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
        
        // Error Details Card (shown when DNS operations fail)
        if (lastDnsError != null) {
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
                        text = "Operation Failed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusError
                    )
                    
                    Text(
                        text = lastDnsError!!,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Button(
                        onClick = { lastDnsError = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SwitchTrack)
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
    
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
        MainScreen(vpnManager = VpnManager(), onHostnameChange = {})
    }
}