package com.example.qr_scan_app

import kotlinx.coroutines.test.runTest
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
    fun checkBad(){
        assertFalse(true)
    }
    @Test
    fun testDb() = runTest{
        val result = DBconnectionExposed("test","test")
        assertFalse(result.isConnected())
    }
}