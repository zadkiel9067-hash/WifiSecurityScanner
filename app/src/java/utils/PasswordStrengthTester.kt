// فایل: app/src/main/java/com/example/wifiscanner/utils/PasswordStrengthTester.kt
package com.example.wifiscanner.utils

data class PasswordStrengthResult(
    val password: String,
    val isWeak: Boolean,
    val strengthScore: Int,
    val suggestions: List<String>,
    val timeToCrack: String
)

class PasswordStrengthTester {
    
    private val weakPasswords = setOf(
        "12345678", "123456789", "password", "12345", "123456",
        "qwerty", "abc123", "111111", "123123", "admin",
        "user", "welcome", "letmein", "passw0rd", "iloveyou",
        "09123456789", "1234", "00000000", "88888888",
        "Iran1402", "Tehran123", "wifi123", "modem123"
    )
    
    fun testPasswordStrength(password: String): PasswordStrengthResult {
        var score = 100
        val suggestions = mutableListOf<String>()
        
        if (weakPasswords.contains(password.lowercase())) {
            score -= 70
            suggestions.add("❌ این رمز در لیست رمزهای ضعیف وجود دارد")
        }
        
        when {
            password.length < 8 -> {
                score -= 40
                suggestions.add("⚠️ رمز کمتر از ۸ کاراکتر است")
            }
            password.length < 12 -> {
                score -= 20
                suggestions.add("📏 رمز می‌تواند بلندتر باشد")
            }
        }
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) }
        
        if (!hasUppercase) {
            score -= 15
            suggestions.add("🔠 حداقل یک حرف بزرگ استفاده کن")
        }
        if (!hasLowercase) {
            score -= 10
            suggestions.add("🔡 حداقل یک حرف کوچک استفاده کن")
        }
        if (!hasDigit) {
            score -= 15
            suggestions.add("🔢 حداقل یک عدد استفاده کن")
        }
        if (!hasSpecial) {
            score -= 20
            suggestions.add("✨ حداقل یک کاراکتر ویژه (!@#$%) استفاده کن")
        }
        
        val timeToCrack = calculateCrackTime(score, password.length)
        
        return PasswordStrengthResult(
            password = maskPassword(password),
            isWeak = score < 60,
            strengthScore = score.coerceIn(0, 100),
            suggestions = suggestions,
            timeToCrack = timeToCrack
        )
    }
    
    private fun calculateCrackTime(score: Int, length: Int): String {
        return when {
            score < 30 -> "کمتر از ۱ ثانیه (بسیار خطرناک)"
            score < 50 -> "کمتر از ۱ دقیقه"
            score < 70 -> "چند ساعت تا چند روز"
            score < 85 -> "چند ماه تا چند سال"
            else -> "میلیون‌ها سال (امن)"
        }
    }
    
    private fun maskPassword(password: String): String {
        return if (password.length > 4) {
            password.substring(0, 2) + "*".repeat(password.length - 4) + password.substring(password.length - 2)
        } else {
            "***"
        }
    }
}
