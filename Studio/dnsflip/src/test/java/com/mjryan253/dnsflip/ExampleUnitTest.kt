package com.mjryan253.dnsflip

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `basic math operations work correctly`() {
        // Test basic arithmetic
        assertEquals(10, 5 + 5)
        assertEquals(25, 5 * 5)
        assertEquals(2, 10 / 5)
        assertEquals(0, 10 % 5)
    }

    @Test
    fun `string operations work correctly`() {
        // Test string operations
        val testString = "DNSFlip"
        assertTrue("String should contain 'DNS'", testString.contains("DNS"))
        assertTrue("String should contain 'Flip'", testString.contains("Flip"))
        assertEquals("dnsflip", testString.lowercase())
        assertEquals("DNSFLIP", testString.uppercase())
    }

    @Test
    fun `list operations work correctly`() {
        // Test list operations
        val dnsServers = listOf("1.1.1.1", "8.8.8.8", "9.9.9.9")
        assertEquals(3, dnsServers.size)
        assertTrue("Should contain Cloudflare DNS", dnsServers.contains("1.1.1.1"))
        assertTrue("Should contain Google DNS", dnsServers.contains("8.8.8.8"))
        assertTrue("Should contain Quad9 DNS", dnsServers.contains("9.9.9.9"))
    }

    @Test
    fun `boolean logic works correctly`() {
        // Test boolean operations
        assertTrue("True should be true", true)
        assertFalse("False should be false", false)
        assertTrue("Not false should be true", !false)
        assertFalse("Not true should be false", !true)
    }
}