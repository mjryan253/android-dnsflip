package com.mjryan253.dnsflip

import android.content.Context
import android.provider.Settings
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for DNSManager class
 * 
 * Tests the core DNS switching functionality including:
 * - DNS mode detection and switching
 * - Permission validation
 * - Error handling
 * - Status descriptions
 */
class DNSManagerTest {

    private lateinit var dnsManager: DNSManager
    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: android.content.ContentResolver

    @Before
    fun setUp() {
        dnsManager = DNSManager()
        mockContext = mockk(relaxed = true)
        mockContentResolver = mockk(relaxed = true)
        
        every { mockContext.contentResolver } returns mockContentResolver
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getCurrentDnsMode returns automatic when mode is opportunistic`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "opportunistic"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns ""

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("automatic", result)
    }

    @Test
    fun `getCurrentDnsMode returns custom when mode is strict with hostname`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "strict"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns "1.1.1.1"

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("custom", result)
    }

    @Test
    fun `getCurrentDnsMode returns automatic when mode is strict but no hostname`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "strict"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns ""

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("automatic", result)
    }

    @Test
    fun `getCurrentDnsMode returns off when mode is off`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "off"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns ""

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("off", result)
    }

    @Test
    fun `getCurrentDnsMode returns unknown when SecurityException is thrown`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("unknown", result)
    }

    @Test
    fun `getCurrentDnsMode returns error when other exception is thrown`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } throws RuntimeException("Unexpected error")

        // When
        val result = dnsManager.getCurrentDnsMode(mockContext)

        // Then
        assertEquals("error", result)
    }

    @Test
    fun `getCurrentDnsHostname returns hostname when available`() {
        // Given
        val expectedHostname = "1.1.1.1"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns expectedHostname

        // When
        val result = dnsManager.getCurrentDnsHostname(mockContext)

        // Then
        assertEquals(expectedHostname, result)
    }

    @Test
    fun `getCurrentDnsHostname returns null when SecurityException is thrown`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.getCurrentDnsHostname(mockContext)

        // Then
        assertNull(result)
    }

    @Test
    fun `setDnsModeOff returns true when successful`() {
        // Given
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "opportunistic") 
        } returns true
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, "") 
        } returns true

        // When
        val result = dnsManager.setDnsModeOff(mockContext)

        // Then
        assertTrue(result)
        verify { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "opportunistic") 
        }
        verify { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, "") 
        }
    }

    @Test
    fun `setDnsModeOff returns false when SecurityException is thrown`() {
        // Given
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "opportunistic") 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.setDnsModeOff(mockContext)

        // Then
        assertFalse(result)
    }

    @Test
    fun `setDnsModeOn returns true when successful`() {
        // Given
        val hostname = "1.1.1.1"
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "strict") 
        } returns true
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, hostname) 
        } returns true

        // When
        val result = dnsManager.setDnsModeOn(mockContext, hostname)

        // Then
        assertTrue(result)
        verify { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "strict") 
        }
        verify { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, hostname) 
        }
    }

    @Test
    fun `setDnsModeOn returns false when hostname is blank`() {
        // When
        val result = dnsManager.setDnsModeOn(mockContext, "")

        // Then
        assertFalse(result)
        verify(exactly = 0) { 
            mockContentResolver.putString(any(), any()) 
        }
    }

    @Test
    fun `setDnsModeOn trims hostname whitespace`() {
        // Given
        val hostname = "  1.1.1.1  "
        val trimmedHostname = "1.1.1.1"
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "strict") 
        } returns true
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, trimmedHostname) 
        } returns true

        // When
        val result = dnsManager.setDnsModeOn(mockContext, hostname)

        // Then
        assertTrue(result)
        verify { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_SPECIFIER, trimmedHostname) 
        }
    }

    @Test
    fun `setDnsModeOn returns false when SecurityException is thrown`() {
        // Given
        val hostname = "1.1.1.1"
        every { 
            mockContentResolver.putString(Settings.Global.PRIVATE_DNS_MODE, "strict") 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.setDnsModeOn(mockContext, hostname)

        // Then
        assertFalse(result)
    }

    @Test
    fun `hasDnsPermission returns true when permission is granted`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "automatic"

        // When
        val result = dnsManager.hasDnsPermission(mockContext)

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasDnsPermission returns false when SecurityException is thrown`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.hasDnsPermission(mockContext)

        // Then
        assertFalse(result)
    }

    @Test
    fun `getDnsStatusDescription returns correct description for off mode`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "off"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns ""

        // When
        val result = dnsManager.getDnsStatusDescription(mockContext)

        // Then
        assertEquals("Status: Off (No DNS protection)", result)
    }

    @Test
    fun `getDnsStatusDescription returns correct description for automatic mode`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "opportunistic"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns ""

        // When
        val result = dnsManager.getDnsStatusDescription(mockContext)

        // Then
        assertEquals("Status: Automatic (System DNS)", result)
    }

    @Test
    fun `getDnsStatusDescription returns correct description for custom mode`() {
        // Given
        val hostname = "1.1.1.1"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } returns "strict"
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_SPECIFIER) 
        } returns hostname

        // When
        val result = dnsManager.getDnsStatusDescription(mockContext)

        // Then
        assertEquals("Status: Custom ($hostname)", result)
    }

    @Test
    fun `getDnsStatusDescription returns correct description for unknown mode`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } throws SecurityException("Permission denied")

        // When
        val result = dnsManager.getDnsStatusDescription(mockContext)

        // Then
        assertEquals("Status: Unknown (Permission required)", result)
    }

    @Test
    fun `getDnsStatusDescription returns correct description for error mode`() {
        // Given
        every { 
            mockContentResolver.getString(Settings.Global.PRIVATE_DNS_MODE) 
        } throws RuntimeException("Unexpected error")

        // When
        val result = dnsManager.getDnsStatusDescription(mockContext)

        // Then
        assertEquals("Status: Error (Check permissions)", result)
    }
}
