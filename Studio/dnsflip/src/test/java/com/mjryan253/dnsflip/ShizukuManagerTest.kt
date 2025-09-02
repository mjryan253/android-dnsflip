package com.mjryan253.dnsflip

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.net.Uri
import android.widget.Toast
import dev.rikka.shizuku.api.Shizuku
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ShizukuManager class
 * 
 * Tests the Shizuku integration functionality including:
 * - Shizuku installation detection
 * - Permission state management
 * - Status messages and actions
 * - Error handling
 */
class ShizukuManagerTest {

    private lateinit var shizukuManager: ShizukuManager
    private lateinit var mockContext: Context
    private lateinit var mockPackageManager: PackageManager
    private lateinit var mockPackageInfo: PackageInfo

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockPackageManager = mockk(relaxed = true)
        mockPackageInfo = mockk(relaxed = true)
        
        every { mockContext.packageManager } returns mockPackageManager
        every { mockContext.startActivity(any()) } just Runs
        every { mockContext.getSystemService(Context.CLIPBOARD_SERVICE) } returns mockk(relaxed = true)
        
        // Mock Shizuku static methods
        mockkStatic(Shizuku::class)
        mockkStatic(Toast::class)
        mockkStatic(Uri::class)
        
        every { Toast.makeText(any(), any<String>(), any()) } returns mockk(relaxed = true)
        every { Uri.parse(any()) } returns mockk(relaxed = true)
        
        shizukuManager = ShizukuManager(mockContext)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkStatic(Shizuku::class)
        unmockkStatic(Toast::class)
        unmockkStatic(Uri::class)
    }

    @Test
    fun `isShizukuInstalled returns true when Shizuku is installed`() {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo

        // When
        val result = shizukuManager.isShizukuInstalled()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isShizukuInstalled returns false when Shizuku is not installed`() {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws PackageManager.NameNotFoundException("Package not found")

        // When
        val result = shizukuManager.isShizukuInstalled()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isShizukuInstalled returns false when exception occurs`() {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws RuntimeException("Unexpected error")

        // When
        val result = shizukuManager.isShizukuInstalled()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getStatusMessage returns correct message for READY state`() = runTest {
        // Given
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_GRANTED

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getStatusMessage()

        // Then
        assertEquals("Shizuku is ready and permission granted", result)
    }

    @Test
    fun `getStatusMessage returns correct message for NOT_INSTALLED state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws PackageManager.NameNotFoundException("Package not found")

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getStatusMessage()

        // Then
        assertEquals("Shizuku is not installed", result)
    }

    @Test
    fun `getStatusMessage returns correct message for NOT_RUNNING state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns false

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getStatusMessage()

        // Then
        assertEquals("Shizuku is installed but not running", result)
    }

    @Test
    fun `getStatusMessage returns correct message for PERMISSION_REQUIRED state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_DENIED

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getStatusMessage()

        // Then
        assertEquals("Shizuku permission denied", result)
    }

    @Test
    fun `getStatusMessage returns correct message for ERROR state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns -1 // Unknown state

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getStatusMessage()

        // Then
        assertEquals("Shizuku permission required", result)
    }

    @Test
    fun `getRecommendedAction returns correct action for READY state`() = runTest {
        // Given
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_GRANTED

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getRecommendedAction()

        // Then
        assertEquals("Ready to use", result)
    }

    @Test
    fun `getRecommendedAction returns correct action for NOT_INSTALLED state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws PackageManager.NameNotFoundException("Package not found")

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getRecommendedAction()

        // Then
        assertEquals("Install Shizuku", result)
    }

    @Test
    fun `getRecommendedAction returns correct action for NOT_RUNNING state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns false

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getRecommendedAction()

        // Then
        assertEquals("Start Shizuku service", result)
    }

    @Test
    fun `getRecommendedAction returns correct action for PERMISSION_REQUIRED state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns -1 // Unknown state

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getRecommendedAction()

        // Then
        assertEquals("Grant permission", result)
    }

    @Test
    fun `getRecommendedAction returns correct action for PERMISSION_DENIED state`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_DENIED

        // When
        shizukuManager.checkShizukuStatus()
        val result = shizukuManager.getRecommendedAction()

        // Then
        assertEquals("Use ADB method", result)
    }

    @Test
    fun `requestPermission does nothing when permission already granted`() {
        // Given
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_GRANTED

        // When
        shizukuManager.requestPermission()

        // Then
        verify(exactly = 0) { Shizuku.requestPermission(any()) }
    }

    @Test
    fun `requestPermission does nothing when permission denied`() {
        // Given
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_DENIED

        // When
        shizukuManager.requestPermission()

        // Then
        verify(exactly = 0) { Shizuku.requestPermission(any()) }
    }

    @Test
    fun `requestPermission calls Shizuku requestPermission when permission required`() {
        // Given
        every { Shizuku.checkSelfPermission() } returns -1 // Unknown state

        // When
        shizukuManager.requestPermission()

        // Then
        verify { Shizuku.requestPermission(0) }
    }

    @Test
    fun `requestPermission handles exception gracefully`() {
        // Given
        every { Shizuku.checkSelfPermission() } returns -1 // Unknown state
        every { Shizuku.requestPermission(any()) } throws RuntimeException("Request failed")

        // When
        shizukuManager.requestPermission()

        // Then
        // Should not throw exception
        verify { Shizuku.requestPermission(0) }
    }

    @Test
    fun `openShizukuApp starts Shizuku activity when available`() {
        // Given
        val mockIntent = mockk<Intent>(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setClassName(any(), any()) } returns mockIntent
        every { anyConstructed<Intent>().flags = any() } just Runs

        // When
        shizukuManager.openShizukuApp()

        // Then
        verify { mockContext.startActivity(any()) }
    }

    @Test
    fun `openShizukuApp falls back to download when activity not available`() {
        // Given
        every { mockContext.startActivity(any()) } throws Exception("Activity not found")
        val mockIntent = mockk<Intent>(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().action = any() } returns mockIntent
        every { anyConstructed<Intent>().data = any() } returns mockIntent
        every { anyConstructed<Intent>().flags = any() } just Runs

        // When
        shizukuManager.openShizukuApp()

        // Then
        verify { mockContext.startActivity(any()) }
    }

    @Test
    fun `openShizukuDownload opens GitHub releases page`() {
        // Given
        val mockIntent = mockk<Intent>(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().action = any() } returns mockIntent
        every { anyConstructed<Intent>().data = any() } returns mockIntent
        every { anyConstructed<Intent>().flags = any() } just Runs

        // When
        shizukuManager.openShizukuDownload()

        // Then
        verify { mockContext.startActivity(any()) }
    }

    @Test
    fun `openShizukuDownload shows toast when intent fails`() {
        // Given
        every { mockContext.startActivity(any()) } throws Exception("No browser available")
        val mockIntent = mockk<Intent>(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().action = any() } returns mockIntent
        every { anyConstructed<Intent>().data = any() } returns mockIntent
        every { anyConstructed<Intent>().flags = any() } just Runs

        // When
        shizukuManager.openShizukuDownload()

        // Then
        verify { Toast.makeText(any(), any<String>(), any()) }
    }

    @Test
    fun `shizukuState flow emits correct state for NOT_INSTALLED`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws PackageManager.NameNotFoundException("Package not found")

        // When
        shizukuManager.checkShizukuStatus()
        val state = shizukuManager.shizukuState.first()

        // Then
        assertEquals(ShizukuState.NOT_INSTALLED, state)
    }

    @Test
    fun `hasPermission flow emits false when Shizuku not installed`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } throws PackageManager.NameNotFoundException("Package not found")

        // When
        shizukuManager.checkShizukuStatus()
        val hasPermission = shizukuManager.hasPermission.first()

        // Then
        assertFalse(hasPermission)
    }

    @Test
    fun `hasPermission flow emits true when permission granted`() = runTest {
        // Given
        every { 
            mockPackageManager.getPackageInfo("moe.shizuku.privileged.api", 0) 
        } returns mockPackageInfo
        every { Shizuku.pingBinder() } returns true
        every { Shizuku.checkSelfPermission() } returns Shizuku.PERMISSION_GRANTED

        // When
        shizukuManager.checkShizukuStatus()
        val hasPermission = shizukuManager.hasPermission.first()

        // Then
        assertTrue(hasPermission)
    }

    @Test
    fun `cleanup removes listeners`() {
        // When
        shizukuManager.cleanup()

        // Then
        verify { Shizuku.removeBinderReceivedListener(any()) }
        verify { Shizuku.removeRequestPermissionResultListener(any()) }
    }
}
