package com.mjryan253.dnsflip

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.After

/**
 * Integration tests for PreferencesManager class
 * 
 * Tests the SharedPreferences functionality on a real Android device including:
 * - Saving and loading preferences
 * - Data persistence across operations
 * - Edge cases and error handling
 */
@RunWith(AndroidJUnit4::class)
class PreferencesManagerIntegrationTest {

    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        preferencesManager = PreferencesManager(context)
        // Clear any existing preferences to ensure clean test state
        preferencesManager.clearPreferences()
    }

    @After
    fun tearDown() {
        // Clean up after each test
        preferencesManager.clearPreferences()
    }

    @Test
    fun `save and load DNS hostname works correctly`() {
        // Given
        val testHostname = "8.8.8.8"

        // When
        preferencesManager.saveDnsHostname(testHostname)
        val loadedHostname = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("Loaded hostname should match saved hostname", testHostname, loadedHostname)
    }

    @Test
    fun `save and load DNS mode works correctly`() {
        // Given
        val testMode = "custom"

        // When
        preferencesManager.saveLastDnsMode(testMode)
        val loadedMode = preferencesManager.loadLastDnsMode()

        // Then
        assertEquals("Loaded mode should match saved mode", testMode, loadedMode)
    }

    @Test
    fun `loadDnsHostname returns default when no preferences exist`() {
        // Given
        // No preferences saved (clean state)

        // When
        val loadedHostname = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("Should return default hostname when no preferences exist", 
            "1.1.1.1", loadedHostname)
    }

    @Test
    fun `loadLastDnsMode returns default when no preferences exist`() {
        // Given
        // No preferences saved (clean state)

        // When
        val loadedMode = preferencesManager.loadLastDnsMode()

        // Then
        assertEquals("Should return default mode when no preferences exist", 
            "automatic", loadedMode)
    }

    @Test
    fun `saveDnsHostname trims whitespace`() {
        // Given
        val hostnameWithWhitespace = "  1.1.1.1  "
        val expectedHostname = "1.1.1.1"

        // When
        preferencesManager.saveDnsHostname(hostnameWithWhitespace)
        val loadedHostname = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("Should trim whitespace from hostname", expectedHostname, loadedHostname)
    }

    @Test
    fun `saveDnsHostname does not save blank hostname`() {
        // Given
        val blankHostname = ""

        // When
        preferencesManager.saveDnsHostname(blankHostname)
        val loadedHostname = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("Should not save blank hostname, should return default", 
            "1.1.1.1", loadedHostname)
    }

    @Test
    fun `saveDnsHostname does not save whitespace-only hostname`() {
        // Given
        val whitespaceHostname = "   "

        // When
        preferencesManager.saveDnsHostname(whitespaceHostname)
        val loadedHostname = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("Should not save whitespace-only hostname, should return default", 
            "1.1.1.1", loadedHostname)
    }

    @Test
    fun `clearPreferences removes all saved data`() {
        // Given
        preferencesManager.saveDnsHostname("8.8.8.8")
        preferencesManager.saveLastDnsMode("custom")

        // When
        preferencesManager.clearPreferences()

        // Then
        assertEquals("Hostname should return default after clear", 
            "1.1.1.1", preferencesManager.loadDnsHostname())
        assertEquals("Mode should return default after clear", 
            "automatic", preferencesManager.loadLastDnsMode())
        assertFalse("Should have no preferences after clear", 
            preferencesManager.hasPreferences())
    }

    @Test
    fun `hasPreferences returns correct state`() {
        // Given
        // Initially no preferences

        // When & Then
        assertFalse("Should have no preferences initially", 
            preferencesManager.hasPreferences())

        // When
        preferencesManager.saveDnsHostname("8.8.8.8")

        // Then
        assertTrue("Should have preferences after saving hostname", 
            preferencesManager.hasPreferences())

        // When
        preferencesManager.clearPreferences()
        preferencesManager.saveLastDnsMode("custom")

        // Then
        assertTrue("Should have preferences after saving mode", 
            preferencesManager.hasPreferences())
    }

    @Test
    fun `getAllPreferences returns all saved data`() {
        // Given
        val hostname = "9.9.9.9"
        val mode = "custom"

        // When
        preferencesManager.saveDnsHostname(hostname)
        preferencesManager.saveLastDnsMode(mode)
        val allPreferences = preferencesManager.getAllPreferences()

        // Then
        assertTrue("Should contain hostname", allPreferences.containsKey("dns_hostname"))
        assertTrue("Should contain mode", allPreferences.containsKey("last_dns_mode"))
        assertEquals("Should contain correct hostname", hostname, allPreferences["dns_hostname"])
        assertEquals("Should contain correct mode", mode, allPreferences["last_dns_mode"])
    }

    @Test
    fun `preferences persist across multiple operations`() {
        // Given
        val hostname1 = "1.1.1.1"
        val hostname2 = "8.8.8.8"
        val mode1 = "custom"
        val mode2 = "automatic"

        // When
        preferencesManager.saveDnsHostname(hostname1)
        preferencesManager.saveLastDnsMode(mode1)
        preferencesManager.saveDnsHostname(hostname2)
        preferencesManager.saveLastDnsMode(mode2)

        // Then
        assertEquals("Should have latest hostname", hostname2, preferencesManager.loadDnsHostname())
        assertEquals("Should have latest mode", mode2, preferencesManager.loadLastDnsMode())
    }

    @Test
    fun `preferences handle edge case values`() {
        val edgeCaseHostnames = listOf(
            "dns.google",
            "cloudflare-dns.com",
            "1.0.0.1",
            "very-long-dns-server-name.example.com",
            "dns_server.local"
        )

        val edgeCaseModes = listOf(
            "custom",
            "automatic",
            "off",
            "unknown",
            "error"
        )

        edgeCaseHostnames.forEach { hostname ->
            preferencesManager.saveDnsHostname(hostname)
            assertEquals("Should handle edge case hostname: $hostname", 
                hostname, preferencesManager.loadDnsHostname())
        }

        edgeCaseModes.forEach { mode ->
            preferencesManager.saveLastDnsMode(mode)
            assertEquals("Should handle edge case mode: $mode", 
                mode, preferencesManager.loadLastDnsMode())
        }
    }

    @Test
    fun `preferences handle rapid save operations`() {
        // When
        repeat(100) { index ->
            preferencesManager.saveDnsHostname("1.1.1.$index")
            preferencesManager.saveLastDnsMode(if (index % 2 == 0) "custom" else "automatic")
        }

        // Then
        assertEquals("Should have last saved hostname", "1.1.1.99", preferencesManager.loadDnsHostname())
        assertEquals("Should have last saved mode", "automatic", preferencesManager.loadLastDnsMode())
    }

    @Test
    fun `preferences handle concurrent-like operations`() {
        // When
        preferencesManager.saveDnsHostname("1.1.1.1")
        preferencesManager.saveLastDnsMode("custom")
        preferencesManager.saveDnsHostname("8.8.8.8")
        preferencesManager.saveLastDnsMode("automatic")
        preferencesManager.saveDnsHostname("9.9.9.9")

        // Then
        assertEquals("Should have final hostname", "9.9.9.9", preferencesManager.loadDnsHostname())
        assertEquals("Should have final mode", "automatic", preferencesManager.loadLastDnsMode())
    }

    @Test
    fun `preferences manager handles null-like inputs gracefully`() {
        // When
        preferencesManager.saveDnsHostname(null ?: "")
        preferencesManager.saveLastDnsMode(null ?: "")

        // Then
        assertEquals("Should handle null hostname gracefully", 
            "1.1.1.1", preferencesManager.loadDnsHostname())
        assertEquals("Should handle null mode gracefully", 
            "automatic", preferencesManager.loadLastDnsMode())
    }

    @Test
    fun `preferences manager uses correct preference file`() {
        // Given
        val testHostname = "test.dns.server"

        // When
        preferencesManager.saveDnsHostname(testHostname)

        // Then
        // Create a new instance to test persistence
        val newPreferencesManager = PreferencesManager(context)
        assertEquals("Should persist across new instances", 
            testHostname, newPreferencesManager.loadDnsHostname())
    }
}
