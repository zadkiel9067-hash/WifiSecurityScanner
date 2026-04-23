// فایل: app/src/main/java/com/example/wifiscanner/utils/EvilTwinDetector.kt
package com.example.wifiscanner.utils

import com.example.wifiscanner.data.model.WifiInfo
import com.example.wifiscanner.data.model.RiskLevel
import com.example.wifiscanner.data.model.EvilTwinResult

class EvilTwinDetector {
    
    fun detectEvilTwins(networks: List<WifiInfo>): List<EvilTwinResult> {
        val groupedBySSID = networks.filter { it.ssid.isNotBlank() && it.ssid != "<unknown ssid>" }
            .groupBy { it.ssid }
        
        val results = mutableListOf<EvilTwinResult>()
        
        groupedBySSID.forEach { (ssid, networksWithSameSSID) ->
            if (networksWithSameSSID.size > 1) {
                val original = findMostSecureNetwork(networksWithSameSSID)
                val fakes = networksWithSameSSID.filter { it.bssid != original.bssid }
                
                val riskLevel = calculateRiskLevel(original, fakes)
                
                if (riskLevel != RiskLevel.SAFE) {
                    results.add(EvilTwinResult(
                        ssid = ssid,
                        originalBSSID = original.bssid,
                        fakeBSSIDs = fakes.map { it.bssid },
                        riskLevel = riskLevel,
                        suggestion = getSuggestion(riskLevel, original.ssid)
                    ))
                }
            }
        }
        
        return results
    }
    
    private fun findMostSecureNetwork(networks: List<WifiInfo>): WifiInfo {
        return networks.maxBy { getSecurityScore(it.capabilities) }
    }
    
    private fun getSecurityScore(capabilities: String): Int {
        return when {
            capabilities.contains("WPA3") -> 100
            capabilities.contains("WPA2") -> 80
            capabilities.contains("WPA") -> 60
            capabilities.contains("WEP") -> 30
            capabilities.contains("OPEN") -> 10
            else -> 50
        }
    }
    
    private fun calculateRiskLevel(original: WifiInfo, fakes: List<WifiInfo>): RiskLevel {
        val isOriginalSecure = getSecurityScore(original.capabilities) > 70
        val hasOpenFake = fakes.any { it.capabilities.contains("OPEN") }
        val hasWeakerFake = fakes.any { 
            getSecurityScore(it.capabilities) < getSecurityScore(original.capabilities) 
        }
        
        return when {
            hasOpenFake -> RiskLevel.CRITICAL
            hasWeakerFake && fakes.size > 1 -> RiskLevel.HIGH
            hasWeakerFake -> RiskLevel.MEDIUM
            else -> RiskLevel.SAFE
        }
    }
    
    private fun getSuggestion(riskLevel: RiskLevel, ssid: String): String {
        return when (riskLevel) {
            RiskLevel.CRITICAL -> "⚠️ هشدار جدی! شبکه دوقلو بدون رمز شناسایی شد"
            RiskLevel.HIGH -> "🔴 چندین شبکه جعلی با امنیت ضعیف وجود دارد"
            RiskLevel.MEDIUM -> "🟡 احتمال وجود شبکه دوقلو، مراقب باش"
            RiskLevel.SAFE -> "✅ شبکه امن است"
        }
    }
}
