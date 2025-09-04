package com.mjryan253.dnsflip

import android.content.Context
import android.content.SharedPreferences
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PreferencesManager class
 * 
 * Tests the data persistence functionality including:
 * - Saving and loading DNS hostname
 * - Saving and loading DNS mode
 * - Preference clearing and validation
 * - Default value handling
 */
class PreferencesManagerTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)
        
        every { mockContext.getSharedPreferences("dnsflip_preferences", Context.MODE_PRIVATE) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just Runs
        every { mockEditor.clear() } returns mockEditor
        
        preferencesManager = PreferencesManager(mockContext)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `saveDnsHostname saves valid hostname`() {
        // Given
        val hostname = "1.1.1.1"

        // When
        preferencesManager.saveDnsHostname(hostname)

        // Then
        verify { mockEditor.putString("dns_hostname", hostname) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `saveDnsHostname trims whitespace from hostname`() {
        // Given
        val hostname = "  1.1.1.1  "
        val trimmedHostname = "1.1.1.1"

        // When
        preferencesManager.saveDnsHostname(hostname)

        // Then
        verify { mockEditor.putString("dns_hostname", trimmedHostname) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `saveDnsHostname does not save blank hostname`() {
        // Given
        val hostname = ""

        // When
        preferencesManager.saveDnsHostname(hostname)

        // Then
        verify(exactly = 0) { mockEditor.putString(any(), any()) }
        verify(exactly = 0) { mockEditor.apply() }
    }

    @Test
    fun `saveDnsHostname does not save whitespace-only hostname`() {
        // Given
        val hostname = "   "

        // When
        preferencesManager.saveDnsHostname(hostname)

        // Then
        verify(exactly = 0) { mockEditor.putString(any(), any()) }
        verify(exactly = 0) { mockEditor.apply() }
    }

    @Test
    fun `loadDnsHostname returns saved hostname`() {
        // Given
        val savedHostname = "8.8.8.8"
        every { mockSharedPreferences.getString("dns_hostname", "1.1.1.1") } returns savedHostname

        // When
        val result = preferencesManager.loadDnsHostname()

        // Then
        assertEquals(savedHostname, result)
    }

    @Test
    fun `loadDnsHostname returns default when no saved hostname`() {
        // Given
        every { mockSharedPreferences.getString("dns_hostname", "1.1.1.1") } returns null

        // When
        val result = preferencesManager.loadDnsHostname()

        // Then
        assertEquals("1.1.1.1", result)
    }

    @Test
    fun `saveLastDnsMode saves valid mode`() {
        // Given
        val mode = "custom"

        // When
        preferencesManager.saveLastDnsMode(mode)

        // Then
        verify { mockEditor.putString("last_dns_mode", mode) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `saveLastDnsMode saves automatic mode`() {
        // Given
        val mode = "automatic"

        // When
        preferencesManager.saveLastDnsMode(mode)

        // Then
        verify { mockEditor.putString("last_dns_mode", mode) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `loadLastDnsMode returns saved mode`() {
        // Given
        val savedMode = "custom"
        every { mockSharedPreferences.getString("last_dns_mode", "automatic") } returns savedMode

        // When
        val result = preferencesManager.loadLastDnsMode()

        // Then
        assertEquals(savedMode, result)
    }

    @Test
    fun `loadLastDnsMode returns default when no saved mode`() {
        // Given
        every { mockSharedPreferences.getString("last_dns_mode", "automatic") } returns null

        // When
        val result = preferencesManager.loadLastDnsMode()

        // Then
        assertEquals("automatic", result)
    }

    @Test
    fun `clearPreferences clears all preferences`() {
        // When
        preferencesManager.clearPreferences()

        // Then
        verify { mockEditor.clear() }
        verify { mockEditor.apply() }
    }

    @Test
    fun `hasPreferences returns true when hostname exists`() {
        // Given
        every { mockSharedPreferences.contains("dns_hostname") } returns true
        every { mockSharedPreferences.contains("last_dns_mode") } returns false

        // When
        val result = preferencesManager.hasPreferences()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasPreferences returns true when mode exists`() {
        // Given
        every { mockSharedPreferences.contains("dns_hostname") } returns false
        every { mockSharedPreferences.contains("last_dns_mode") } returns true

        // When
        val result = preferencesManager.hasPreferences()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasPreferences returns true when both exist`() {
        // Given
        every { mockSharedPreferences.contains("dns_hostname") } returns true
        every { mockSharedPreferences.contains("last_dns_mode") } returns true

        // When
        val result = preferencesManager.hasPreferences()

        // Then
        assertTrue(result)
    }

    @Test
    fun `hasPreferences returns false when no preferences exist`() {
        // Given
        every { mockSharedPreferences.contains("dns_hostname") } returns false
        every { mockSharedPreferences.contains("last_dns_mode") } returns false

        // When
        val result = preferencesManager.hasPreferences()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getAllPreferences returns all preferences`() {
        // Given
        val expectedPreferences = mapOf(
            "dns_hostname" to "1.1.1.1",
            "last_dns_mode" to "custom"
        )
        every { mockSharedPreferences.all } returns expectedPreferences

        // When
        val result = preferencesManager.getAllPreferences()

        // Then
        assertEquals(expectedPreferences, result)
    }

    @Test
    fun `getAllPreferences returns empty map when no preferences`() {
        // Given
        every { mockSharedPreferences.all } returns emptyMap<String, Any?>()

        // When
        val result = preferencesManager.getAllPreferences()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `preferences manager uses correct preference name`() {
        // When
        preferencesManager = PreferencesManager(mockContext)

        // Then
        verify { mockContext.getSharedPreferences("dnsflip_preferences", Context.MODE_PRIVATE) }
    }

    @Test
    fun `saveDnsHostname handles null input gracefully`() {
        // Given
        val hostname: String? = null

        // When
        preferencesManager.saveDnsHostname(hostname ?: "")

        // Then
        verify(exactly = 0) { mockEditor.putString(any(), any()) }
        verify(exactly = 0) { mockEditor.apply() }
    }

    @Test
    fun `saveLastDnsMode handles null input gracefully`() {
        // Given
        val mode: String? = null

        // When
        preferencesManager.saveLastDnsMode(mode ?: "")

        // Then
        verify { mockEditor.putString("last_dns_mode", "") }
        verify { mockEditor.apply() }
    }
}
