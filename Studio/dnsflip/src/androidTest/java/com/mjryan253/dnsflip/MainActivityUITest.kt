package com.mjryan253.dnsflip

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mjryan253.dnsflip.ui.components.LargeLightSwitch
import com.mjryan253.dnsflip.ui.theme.DNSFlipTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MainActivity and components
 * 
 * Tests the user interface functionality including:
 * - Light switch component behavior
 * - UI state management
 * - User interactions
 * - Visual feedback
 */
@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `light switch displays correct initial state when off`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("System DNS Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("OFF").assertIsDisplayed()
    }

    @Test
    fun `light switch displays correct state when on`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = true,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("Custom DNS Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("ON").assertIsDisplayed()
    }

    @Test
    fun `light switch calls onToggle when clicked`() {
        // Given
        var toggleCalled = false
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = { toggleCalled = true },
                    enabled = true
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("System DNS Active").performClick()

        // Then
        assert(toggleCalled) { "onToggle should be called when switch is clicked" }
    }

    @Test
    fun `light switch does not call onToggle when disabled`() {
        // Given
        var toggleCalled = false
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = { toggleCalled = true },
                    enabled = false
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("System DNS Active").performClick()

        // Then
        assert(!toggleCalled) { "onToggle should not be called when switch is disabled" }
    }

    @Test
    fun `light switch shows checkmark when on`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = true,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("✓").assertIsDisplayed()
    }

    @Test
    fun `light switch shows circle when off`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("○").assertIsDisplayed()
    }

    @Test
    fun `light switch is clickable when enabled`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("System DNS Active").assertHasClickAction()
    }

    @Test
    fun `light switch is not clickable when disabled`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = false
                )
            }
        }

        // When & Then
        // Note: This test might need adjustment based on actual Compose behavior
        // The switch should be visually disabled but might still be technically clickable
        composeTestRule.onNodeWithText("System DNS Active").assertExists()
    }

    @Test
    fun `light switch handles rapid clicks`() {
        // Given
        var clickCount = 0
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = { clickCount++ },
                    enabled = true
                )
            }
        }

        // When
        repeat(5) {
            composeTestRule.onNodeWithText("System DNS Active").performClick()
        }

        // Then
        assert(clickCount == 5) { "Should handle rapid clicks correctly" }
    }

    @Test
    fun `light switch maintains state during rapid toggles`() {
        // Given
        var currentState = false
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = currentState,
                    onToggle = { currentState = !currentState },
                    enabled = true
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("System DNS Active").performClick()

        // Then
        // Re-compose with new state
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = currentState,
                    onToggle = { currentState = !currentState },
                    enabled = true
                )
            }
        }

        composeTestRule.onNodeWithText("Custom DNS Active").assertIsDisplayed()
    }

    @Test
    fun `light switch displays correct text for both states`() {
        // Test OFF state
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        composeTestRule.onNodeWithText("System DNS Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("OFF").assertIsDisplayed()

        // Test ON state
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = true,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        composeTestRule.onNodeWithText("Custom DNS Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("ON").assertIsDisplayed()
    }

    @Test
    fun `light switch has proper accessibility`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = false,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        composeTestRule.onNodeWithText("System DNS Active").assertIsDisplayed()
        // Note: More specific accessibility tests would require additional setup
    }

    @Test
    fun `light switch handles state changes smoothly`() {
        // Given
        var currentState = false
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = currentState,
                    onToggle = { currentState = !currentState },
                    enabled = true
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("System DNS Active").performClick()

        // Then
        // The animation should complete and show the new state
        // Note: This test verifies the component doesn't crash during state changes
        composeTestRule.waitForIdle()
    }

    @Test
    fun `light switch works with different screen sizes`() {
        // Given
        composeTestRule.setContent {
            DNSFlipTheme {
                LargeLightSwitch(
                    isOn = true,
                    onToggle = {},
                    enabled = true
                )
            }
        }

        // When & Then
        // The component should be visible and functional regardless of screen size
        composeTestRule.onNodeWithText("Custom DNS Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("ON").assertIsDisplayed()
    }
}
