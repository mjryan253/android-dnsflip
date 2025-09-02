package com.mjryan253.dnsflip

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mjryan253.dnsflip", appContext.packageName)
    }

    @Test
    fun `app context has correct package name`() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertEquals("Package name should match", "com.mjryan253.dnsflip", appContext.packageName)
    }

    @Test
    fun `app context is not null`() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When & Then
        assertNotNull("App context should not be null", appContext)
    }

    @Test
    fun `app context has application info`() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        val applicationInfo = appContext.applicationInfo

        // Then
        assertNotNull("Application info should not be null", applicationInfo)
        assertEquals("Package name should match", "com.mjryan253.dnsflip", applicationInfo.packageName)
    }

    @Test
    fun `app context has resources`() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        val resources = appContext.resources

        // Then
        assertNotNull("Resources should not be null", resources)
    }

    @Test
    fun `app context has package manager`() {
        // Given
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        val packageManager = appContext.packageManager

        // Then
        assertNotNull("Package manager should not be null", packageManager)
    }
}