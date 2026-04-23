// فایل: app/src/main/java/com/example/wifiscanner/data/model/WifiInfo.kt
package com.example.wifiscanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_networks")
data class WifiInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val capabilities: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    val signalStrength: String
        get() = when {
            rssi > -50 -> "عالی 📶📶📶"
            rssi > -65 -> "خوب 📶📶"
            rssi > -75 -> "متوسط 📶"
            rssi > -85 -> "ضعیف 📶"
            else -> "بسیار ضعیف 📶"
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
