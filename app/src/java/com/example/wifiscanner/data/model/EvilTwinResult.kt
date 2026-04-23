// فایل: app/src/main/java/com/example/wifiscanner/data/model/EvilTwinResult.kt
package com.example.wifiscanner.data.model

enum class RiskLevel {
    CRITICAL, HIGH, MEDIUM, SAFE
}

data class EvilTwinResult(
    val ssid: String,
    val originalBSSID: String,
    val fakeBSSIDs: List<String>,
    val riskLevel: RiskLevel,
    val suggestion: String
)
