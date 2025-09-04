package com.mjryan253.dnsflip.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mjryan253.dnsflip.ui.theme.*


@Composable
fun LargeLightSwitch(
    isOn: Boolean,
    onToggle: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val switchWidth = 280.dp
    val switchHeight = 140.dp
    val thumbSize = 120.dp
    val padding = 10.dp
    
    val thumbOffset by animateFloatAsState(
        targetValue = if (isOn) (switchWidth - thumbSize - padding).value else padding.value,
        animationSpec = tween(durationMillis = 400),
        label = "thumb_animation"
    )
    
    val trackColor = if (isOn) SwitchOn else SwitchTrack
    val thumbColor = if (isOn) SwitchOn else SwitchOff
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main switch
        Box(
            modifier = Modifier
                .size(switchWidth, switchHeight)
                .clip(RoundedCornerShape(70.dp))
                .background(trackColor)
                .border(
                    width = 3.dp,
                    color = trackColor,
                    shape = RoundedCornerShape(70.dp)
                )
                .clickable(enabled = enabled) { onToggle() },
            contentAlignment = Alignment.CenterStart
        ) {
            // Thumb
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .offset(x = thumbOffset.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = thumbColor.copy(alpha = 0.4f),
                        spotColor = thumbColor.copy(alpha = 0.4f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Icon inside thumb
                Text(
                    text = if (isOn) "✓" else "○",
                    color = if (isOn) OLEDBlack else TextSecondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Status text below switch
        Text(
            text = if (isOn) "Custom DNS Active" else "System DNS Active",
            color = if (isOn) SwitchOn else TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
