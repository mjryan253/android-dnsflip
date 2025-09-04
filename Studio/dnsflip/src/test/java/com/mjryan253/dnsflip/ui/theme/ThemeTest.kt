package com.mjryan253.dnsflip.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for theme system
 * 
 * Tests the color definitions and theme consistency including:
 * - OLED-optimized color values
 * - Color accessibility and contrast
 * - Theme consistency
 */
class ThemeTest {

    @Test
    fun `OLED black color is true black`() {
        // Given & When
        val oledBlack = OLEDBlack

        // Then
        assertEquals(Color.Black, oledBlack)
        assertEquals(0f, oledBlack.red, 0.001f)
        assertEquals(0f, oledBlack.green, 0.001f)
        assertEquals(0f, oledBlack.blue, 0.001f)
        assertEquals(1f, oledBlack.alpha, 0.001f)
    }

    @Test
    fun `surface dark color is dark gray`() {
        // Given & When
        val surfaceDark = SurfaceDark

        // Then
        assertTrue("Surface dark should be darker than 50% gray", surfaceDark.red < 0.5f)
        assertTrue("Surface dark should be darker than 50% gray", surfaceDark.green < 0.5f)
        assertTrue("Surface dark should be darker than 50% gray", surfaceDark.blue < 0.5f)
        assertEquals(1f, surfaceDark.alpha, 0.001f)
    }

    @Test
    fun `surface variant color is darker than surface dark`() {
        // Given & When
        val surfaceVariant = SurfaceVariant
        val surfaceDark = SurfaceDark

        // Then
        assertTrue("Surface variant should be darker than surface dark", 
            surfaceVariant.red < surfaceDark.red)
        assertTrue("Surface variant should be darker than surface dark", 
            surfaceVariant.green < surfaceDark.green)
        assertTrue("Surface variant should be darker than surface dark", 
            surfaceVariant.blue < surfaceDark.blue)
    }

    @Test
    fun `switch on color is bright green`() {
        // Given & When
        val switchOn = SwitchOn

        // Then
        assertTrue("Switch on should be bright green", switchOn.green > 0.8f)
        assertTrue("Switch on should have low red component", switchOn.red < 0.2f)
        assertTrue("Switch on should have low blue component", switchOn.blue < 0.2f)
        assertEquals(1f, switchOn.alpha, 0.001f)
    }

    @Test
    fun `switch off color is dark gray`() {
        // Given & When
        val switchOff = SwitchOff

        // Then
        assertTrue("Switch off should be dark", switchOff.red < 0.5f)
        assertTrue("Switch off should be dark", switchOff.green < 0.5f)
        assertTrue("Switch off should be dark", switchOff.blue < 0.5f)
        assertEquals(1f, switchOff.alpha, 0.001f)
    }

    @Test
    fun `switch track color is medium gray`() {
        // Given & When
        val switchTrack = SwitchTrack

        // Then
        assertTrue("Switch track should be medium gray", switchTrack.red > 0.2f)
        assertTrue("Switch track should be medium gray", switchTrack.red < 0.8f)
        assertTrue("Switch track should be gray (equal RGB components)", 
            kotlin.math.abs(switchTrack.red - switchTrack.green) < 0.1f)
        assertTrue("Switch track should be gray (equal RGB components)", 
            kotlin.math.abs(switchTrack.green - switchTrack.blue) < 0.1f)
        assertEquals(1f, switchTrack.alpha, 0.001f)
    }

    @Test
    fun `text primary color is light`() {
        // Given & When
        val textPrimary = TextPrimary

        // Then
        assertTrue("Text primary should be light", textPrimary.red > 0.8f)
        assertTrue("Text primary should be light", textPrimary.green > 0.8f)
        assertTrue("Text primary should be light", textPrimary.blue > 0.8f)
        assertEquals(1f, textPrimary.alpha, 0.001f)
    }

    @Test
    fun `text secondary color is medium gray`() {
        // Given & When
        val textSecondary = TextSecondary

        // Then
        assertTrue("Text secondary should be medium gray", textSecondary.red > 0.3f)
        assertTrue("Text secondary should be medium gray", textSecondary.red < 0.7f)
        assertTrue("Text secondary should be gray (equal RGB components)", 
            kotlin.math.abs(textSecondary.red - textSecondary.green) < 0.1f)
        assertTrue("Text secondary should be gray (equal RGB components)", 
            kotlin.math.abs(textSecondary.green - textSecondary.blue) < 0.1f)
        assertEquals(1f, textSecondary.alpha, 0.001f)
    }

    @Test
    fun `status success color is green`() {
        // Given & When
        val statusSuccess = StatusSuccess

        // Then
        assertTrue("Status success should be green", statusSuccess.green > 0.7f)
        assertTrue("Status success should have low red component", statusSuccess.red < 0.3f)
        assertTrue("Status success should have low blue component", statusSuccess.blue < 0.3f)
        assertEquals(1f, statusSuccess.alpha, 0.001f)
    }

    @Test
    fun `status warning color is orange or yellow`() {
        // Given & When
        val statusWarning = StatusWarning

        // Then
        assertTrue("Status warning should be warm color", statusWarning.red > 0.5f)
        assertTrue("Status warning should be warm color", statusWarning.green > 0.3f)
        assertTrue("Status warning should have low blue component", statusWarning.blue < 0.3f)
        assertEquals(1f, statusWarning.alpha, 0.001f)
    }

    @Test
    fun `status error color is red`() {
        // Given & When
        val statusError = StatusError

        // Then
        assertTrue("Status error should be red", statusError.red > 0.7f)
        assertTrue("Status error should have low green component", statusError.green < 0.3f)
        assertTrue("Status error should have low blue component", statusError.blue < 0.3f)
        assertEquals(1f, statusError.alpha, 0.001f)
    }

    @Test
    fun `all colors have full alpha`() {
        // Given
        val colors = listOf(
            OLEDBlack, SurfaceDark, SurfaceVariant,
            SwitchOn, SwitchOff, SwitchTrack,
            TextPrimary, TextSecondary,
            StatusSuccess, StatusWarning, StatusError
        )

        // When & Then
        colors.forEach { color ->
            assertEquals("Color $color should have full alpha", 1f, color.alpha, 0.001f)
        }
    }

    @Test
    fun `switch colors provide good contrast`() {
        // Given
        val switchOn = SwitchOn
        val switchOff = SwitchOff
        val switchTrack = SwitchTrack

        // When & Then
        // Switch on should be significantly brighter than switch off
        val onBrightness = (switchOn.red + switchOn.green + switchOn.blue) / 3f
        val offBrightness = (switchOff.red + switchOff.green + switchOff.blue) / 3f
        assertTrue("Switch on should be brighter than switch off", 
            onBrightness > offBrightness + 0.3f)

        // Track should be between on and off
        val trackBrightness = (switchTrack.red + switchTrack.green + switchTrack.blue) / 3f
        assertTrue("Track should be between on and off brightness", 
            trackBrightness > offBrightness && trackBrightness < onBrightness)
    }

    @Test
    fun `text colors provide good contrast against dark backgrounds`() {
        // Given
        val textPrimary = TextPrimary
        val textSecondary = TextSecondary
        val surfaceDark = SurfaceDark

        // When & Then
        // Primary text should be bright enough for good contrast
        val primaryBrightness = (textPrimary.red + textPrimary.green + textPrimary.blue) / 3f
        val surfaceBrightness = (surfaceDark.red + surfaceDark.green + surfaceDark.blue) / 3f
        assertTrue("Primary text should have good contrast against dark surface", 
            primaryBrightness > surfaceBrightness + 0.5f)

        // Secondary text should be dimmer than primary but still readable
        val secondaryBrightness = (textSecondary.red + textSecondary.green + textSecondary.blue) / 3f
        assertTrue("Secondary text should be dimmer than primary", 
            secondaryBrightness < primaryBrightness)
        assertTrue("Secondary text should still be readable", 
            secondaryBrightness > surfaceBrightness + 0.2f)
    }

    @Test
    fun `status colors are distinct and meaningful`() {
        // Given
        val statusSuccess = StatusSuccess
        val statusWarning = StatusWarning
        val statusError = StatusError

        // When & Then
        // Success should be green
        assertTrue("Success should be green", statusSuccess.green > statusSuccess.red)
        assertTrue("Success should be green", statusSuccess.green > statusSuccess.blue)

        // Warning should be warm (red/orange/yellow)
        assertTrue("Warning should be warm", statusWarning.red > 0.5f)
        assertTrue("Warning should be warm", statusWarning.green > 0.3f)

        // Error should be red
        assertTrue("Error should be red", statusError.red > statusError.green)
        assertTrue("Error should be red", statusError.red > statusError.blue)

        // All should be distinct from each other
        val successBrightness = (statusSuccess.red + statusSuccess.green + statusSuccess.blue) / 3f
        val warningBrightness = (statusWarning.red + statusWarning.green + statusWarning.blue) / 3f
        val errorBrightness = (statusError.red + statusError.green + statusError.blue) / 3f
        
        assertTrue("Status colors should have different brightness levels", 
            kotlin.math.abs(successBrightness - warningBrightness) > 0.1f)
        assertTrue("Status colors should have different brightness levels", 
            kotlin.math.abs(warningBrightness - errorBrightness) > 0.1f)
    }
}
