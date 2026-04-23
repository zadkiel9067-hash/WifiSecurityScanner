// فایل: app/src/test/java/com/example/wifiscanner/EvilTwinDetectorTest.kt
package com.example.wifiscanner

import com.example.wifiscanner.data.model.WifiInfo
import com.example.wifiscanner.utils.EvilTwinDetector
import org.junit.Assert.*
import org.junit.Test

class EvilTwinDetectorTest {
    
    @Test
    fun testEvilTwinDetection() {
        val detector = EvilTwinDetector()
        
        val networks = listOf(
            WifiInfo(ssid = "HomeWiFi", bssid = "AA:BB:CC:DD:EE:FF", rssi = -45, capabilities = "WPA2"),
            WifiInfo(ssid = "HomeWiFi", bssid = "11:22:33:44:55:66", rssi = -60, capabilities = "OPEN")
        )
        
        val result = detector.detectEvilTwins(networks)
        
        assertTrue(result.isNotEmpty())
        assertEquals("HomeWiFi", result[0].ssid)
    }
}
