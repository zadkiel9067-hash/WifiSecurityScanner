// فایل: app/src/main/java/com/example/wifiscanner/data/model/WifiInfo.kt
package com.example.wifiscanner.data.model

data class WifiInfo(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val capabilities: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val signalStrength: String
        get() = when {
            rssi > -50 -> "عالی 📶📶📶"
            rssi > -70 -> "خوب 📶📶"
            rssi > -85 -> "متوسط 📶"
            else -> "ضعیف 📶"
        }
    
    val signalLevel: Int
        get() = when {
            rssi > -50 -> 4
            rssi > -65 -> 3
            rssi > -75 -> 2
            rssi > -85 -> 1
            else -> 0
        }
    
    val securityType: String
        get() = when {
            capabilities.contains("WPA3") -> "WPA3 🔐 (امن)"
            capabilities.contains("WPA2") -> "WPA2 🔐 (امن)"
            capabilities.contains("WPA") -> "WPA ⚠️ (متوسط)"
            capabilities.contains("WEP") -> "WEP ❌ (ضعیف)"
            capabilities.contains("OPEN") -> "بدون رمز 🚨 (خطرناک)"
            else -> "ناشناخته ❓"
        }
    
    val isSecure: Boolean
        get() = securityType.contains("WPA2") || securityType.contains("WPA3")
}
