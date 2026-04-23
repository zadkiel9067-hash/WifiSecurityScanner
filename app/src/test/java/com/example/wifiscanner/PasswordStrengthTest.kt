// فایل: app/src/test/java/com/example/wifiscanner/PasswordStrengthTest.kt
package com.example.wifiscanner

import com.example.wifiscanner.utils.PasswordStrengthTester
import org.junit.Assert.*
import org.junit.Test

class PasswordStrengthTest {
    
    @Test
    fun testWeakPassword() {
        val tester = PasswordStrengthTester()
        val result = tester.testPasswordStrength("12345678")
        
        assertTrue(result.isWeak)
        assertTrue(result.strengthScore < 60)
    }
    
    @Test
    fun testStrongPassword() {
        val tester = PasswordStrengthTester()
        val result = tester.testPasswordStrength("MyStr0ng!P@ssw0rd2024")
        
        assertFalse(result.isWeak)
        assertTrue(result.strengthScore > 70)
    }
}
