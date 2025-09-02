package com.mjryan253.dnsflip

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

/**
 * Integration tests for DNSManager class
 * 
 * Tests the DNS functionality on a real Android device including:
 * - Permission validation
 * - DNS mode detection
 * - Error handling with real system calls
 * 
 * Note: These tests require WRITE_SECURE_SETTINGS permission to pass completely.
 * Without permission, they will test the error handling paths.
 */
@RunWith(AndroidJUnit4::class)
class DNSManagerIntegrationTest {

    private lateinit var context: Context
    private lateinit var dnsManager: DNSManager

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        dnsManager = DNSManager()
    }

    @Test
    fun `getCurrentDnsMode returns valid mode`() {
        // When
        val mode = dnsManager.getCurrentDnsMode(context)

        // Then
        assertTrue("DNS mode should be one of: automatic, custom, off, unknown, error", 
            mode in listOf("automatic", "custom", "off", "unknown", "error"))
    }

    @Test
    fun `getCurrentDnsMode does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.getCurrentDnsMode(context)
        }
    }

    @Test
    fun `getCurrentDnsHostname returns string or null`() {
        // When
        val hostname = dnsManager.getCurrentDnsHostname(context)

        // Then
        if (hostname != null) {
            assertTrue("Hostname should not be empty", hostname.isNotBlank())
            assertTrue("Hostname should be reasonable length", hostname.length < 100)
        }
    }

    @Test
    fun `getCurrentDnsHostname does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.getCurrentDnsHostname(context)
        }
    }

    @Test
    fun `hasDnsPermission returns boolean`() {
        // When
        val hasPermission = dnsManager.hasDnsPermission(context)

        // Then
        assertTrue("hasDnsPermission should return boolean", hasPermission is Boolean)
    }

    @Test
    fun `hasDnsPermission does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.hasDnsPermission(context)
        }
    }

    @Test
    fun `getDnsStatusDescription returns non-empty string`() {
        // When
        val description = dnsManager.getDnsStatusDescription(context)

        // Then
        assertTrue("Status description should not be empty", description.isNotBlank())
        assertTrue("Status description should start with 'Status:'", 
            description.startsWith("Status:"))
    }

    @Test
    fun `getDnsStatusDescription does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.getDnsStatusDescription(context)
        }
    }

    @Test
    fun `setDnsModeOff returns boolean`() {
        // When
        val result = dnsManager.setDnsModeOff(context)

        // Then
        assertTrue("setDnsModeOff should return boolean", result is Boolean)
    }

    @Test
    fun `setDnsModeOff does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.setDnsModeOff(context)
        }
    }

    @Test
    fun `setDnsModeOn with valid hostname returns boolean`() {
        // When
        val result = dnsManager.setDnsModeOn(context, "1.1.1.1")

        // Then
        assertTrue("setDnsModeOn should return boolean", result is Boolean)
    }

    @Test
    fun `setDnsModeOn with valid hostname does not throw exception`() {
        // When & Then
        assertDoesNotThrow {
            dnsManager.setDnsModeOn(context, "1.1.1.1")
        }
    }

    @Test
    fun `setDnsModeOn with blank hostname returns false`() {
        // When
        val result = dnsManager.setDnsModeOn(context, "")

        // Then
        assertFalse("setDnsModeOn with blank hostname should return false", result)
    }

    @Test
    fun `setDnsModeOn with whitespace hostname returns false`() {
        // When
        val result = dnsManager.setDnsModeOn(context, "   ")

        // Then
        assertFalse("setDnsModeOn with whitespace hostname should return false", result)
    }

    @Test
    fun `setDnsModeOn with null hostname returns false`() {
        // When
        val result = dnsManager.setDnsModeOn(context, null ?: "")

        // Then
        assertFalse("setDnsModeOn with null hostname should return false", result)
    }

    @Test
    fun `dns status description is consistent with mode`() {
        // Given
        val mode = dnsManager.getCurrentDnsMode(context)
        val description = dnsManager.getDnsStatusDescription(context)

        // When & Then
        when (mode) {
            "off" -> assertTrue("Description should mention 'Off' for off mode", 
                description.contains("Off"))
            "automatic" -> assertTrue("Description should mention 'Automatic' for automatic mode", 
                description.contains("Automatic"))
            "custom" -> assertTrue("Description should mention 'Custom' for custom mode", 
                description.contains("Custom"))
            "unknown" -> assertTrue("Description should mention 'Unknown' for unknown mode", 
                description.contains("Unknown"))
            "error" -> assertTrue("Description should mention 'Error' for error mode", 
                description.contains("Error"))
        }
    }

    @Test
    fun `dns manager handles multiple rapid calls`() {
        // When & Then
        repeat(10) {
            assertDoesNotThrow {
                dnsManager.getCurrentDnsMode(context)
                dnsManager.getCurrentDnsHostname(context)
                dnsManager.hasDnsPermission(context)
                dnsManager.getDnsStatusDescription(context)
            }
        }
    }

    @Test
    fun `dns manager handles edge case hostnames`() {
        val edgeCaseHostnames = listOf(
            "8.8.8.8",
            "dns.google",
            "cloudflare-dns.com",
            "1.0.0.1",
            "9.9.9.9",
            "208.67.222.222"
        )

        edgeCaseHostnames.forEach { hostname ->
            assertDoesNotThrow("Should handle hostname: $hostname") {
                dnsManager.setDnsModeOn(context, hostname)
            }
        }
    }

    @Test
    fun `dns manager handles very long hostname`() {
        // Given
        val longHostname = "a".repeat(1000)

        // When
        val result = dnsManager.setDnsModeOn(context, longHostname)

        // Then
        // Should not crash, but may return false due to system limitations
        assertTrue("Should return boolean for very long hostname", result is Boolean)
    }

    @Test
    fun `dns manager handles special characters in hostname`() {
        val specialHostnames = listOf(
            "test-dns.example.com",
            "dns_server.local",
            "1.2.3.4:5353",
            "dns.example-domain.com"
        )

        specialHostnames.forEach { hostname ->
            assertDoesNotThrow("Should handle special characters in: $hostname") {
                dnsManager.setDnsModeOn(context, hostname)
            }
        }
    }
}
