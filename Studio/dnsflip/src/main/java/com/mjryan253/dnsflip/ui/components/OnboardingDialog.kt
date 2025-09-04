package com.mjryan253.dnsflip.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mjryan253.dnsflip.ShizukuManager
import com.mjryan253.dnsflip.ShizukuState
import com.mjryan253.dnsflip.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    shizukuManager: ShizukuManager,
    context: Context,
    onPermissionGranted: () -> Unit
) {
    var showAdbInstructions by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<String?>(null) }
    
    val shizukuState by shizukuManager.shizukuState.collectAsState()
    val hasShizukuPermission by shizukuManager.hasPermission.collectAsState()
    
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Setup Required",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary
                            )
                        }
                    }
                    
                    Text(
                        text = "DNSFlip needs special permission to change DNS settings. Choose your preferred method:",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        lineHeight = 24.sp
                    )
                    
                    // Shizuku Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Method 1: Shizuku (Recommended)",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SwitchOn
                                    )
                                    Text(
                                        text = shizukuManager.getStatusMessage(),
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Recommended",
                                    tint = StatusWarning,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            // Shizuku Status
                            when (shizukuState) {
                                ShizukuState.NOT_RUNNING -> {
                                    Button(
                                        onClick = { /* TODO: Implement Shizuku service start once dependencies resolved */ },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Start Shizuku Service")
                                    }
                                }
                                ShizukuState.PERMISSION_REQUIRED -> {
                                    Button(
                                        onClick = { shizukuManager.requestPermission(context) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Grant Permission")
                                    }
                                }
                                ShizukuState.READY -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✓ Ready to use!",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = StatusSuccess
                                        )
                                        Button(
                                            onClick = onPermissionGranted,
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                                        ) {
                                            Text("Continue")
                                        }
                                    }
                                }
                                else -> {
                                    Text(
                                        text = "Shizuku not available. Use ADB method below.",
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            // Shizuku Info
                            if (expandedSection == "shizuku") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Shizuku is a system service that allows apps to use system APIs safely. It's the easiest way to grant permissions.",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Benefits:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "• User-friendly interface\n• No command line needed\n• Persistent permissions\n• Trusted by the community",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            } else {
                                TextButton(
                                    onClick = { expandedSection = "shizuku" },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Learn more about Shizuku")
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                                }
                            }
                        }
                    }
                    
                    // ADB Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Method 2: ADB (Manual)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            
                            Text(
                                text = "Use Android Debug Bridge to grant permission manually.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            
                            Button(
                                onClick = { showAdbInstructions = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SwitchOff)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Show ADB Instructions")
                            }
                            
                            // Quick ADB Command
                            Card(
                                colors = CardDefaults.cardColors(containerColor = OLEDBlack),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Quick Command:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS",
                                        fontSize = 12.sp,
                                        color = StatusSuccess,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText(
                                                "ADB Command",
                                                "adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS"
                                            )
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "ADB command copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Copy Command")
                                    }
                                }
                            }
                        }
                    }
                    
                    // Help Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = StatusError.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = StatusError
                                )
                                Text(
                                    text = "Need Help?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusError
                                )
                            }
                            Text(
                                text = "Check the documentation for detailed setup instructions and troubleshooting tips.",
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/your-repo/dnsflip"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View Documentation")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // ADB Instructions Dialog
    if (showAdbInstructions) {
        AdbInstructionsDialog(
            onDismiss = { showAdbInstructions = false }
        )
    }
}

@Composable
fun AdbInstructionsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADB Setup Instructions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }
                
                Text(
                    text = "Follow these steps to grant permission using ADB:",
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                
                // Step 1
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Step 1: Enable Developer Options",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "• Go to Settings → About phone\n• Tap 'Build number' 7 times\n• You'll see 'You are now a developer!'",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                // Step 2
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Step 2: Enable USB Debugging",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "• Go to Settings → Developer options\n• Enable 'USB debugging'\n• Connect your device to computer",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                // Step 3
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Step 3: Run ADB Command",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Open terminal/command prompt and run:",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = OLEDBlack),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS",
                                fontSize = 12.sp,
                                color = StatusSuccess,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "ADB Command",
                                    "adb shell pm grant com.mjryan253.dnsflip android.permission.WRITE_SECURE_SETTINGS"
                                )
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Command")
                        }
                    }
                }
                
                // Wireless Debugging Option
                Card(
                    colors = CardDefaults.cardColors(containerColor = StatusWarning.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Wireless Debugging (Android 11+)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusWarning
                        )
                        Text(
                            text = "• Enable 'Wireless debugging' in Developer options\n• Note the IP address and port\n• Run: adb connect IP:PORT\n• Then run the grant command",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SwitchOn)
                ) {
                    Text("Got it!")
                }
            }
        }
    }
}
