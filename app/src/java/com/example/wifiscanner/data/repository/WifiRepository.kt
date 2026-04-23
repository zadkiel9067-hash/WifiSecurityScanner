// فایل: app/src/main/java/com/example/wifiscanner/data/repository/WifiRepository.kt
package com.example.wifiscanner.data.repository

import android.content.Context
import android.net.wifi.WifiManager
import com.example.wifiscanner.data.local.WifiDao
import com.example.wifiscanner.data.local.WifiDatabase
import com.example.wifiscanner.data.model.WifiInfo
import kotlinx.coroutines.flow.Flow

class WifiRepository(context: Context) {
    private val wifiDao: WifiDao = WifiDatabase.getDatabase(context).wifiDao()
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    
    fun getAllScans(): Flow<List<WifiInfo>> = wifiDao.getAllScans()
    
    suspend fun saveScanResults(results: List<WifiInfo>) {
        wifiDao.insertAll(results)
    }
    
    suspend fun clearHistory() {
        wifiDao.deleteAll()
    }
    
    fun scanNetworks(): List<WifiInfo> {
        val success = wifiManager.startScan()
        if (!success) return emptyList()
        
        Thread.sleep(2000)
        
        return wifiManager.scanResults
            .filter { it.SSID.isNotBlank() && it.SSID != "<unknown ssid>" }
            .map {
                WifiInfo(
                    ssid = it.SSID,
                    bssid = it.BSSID,
                    rssi = it.level,
                    capabilities = it.capabilities,
                    timestamp = System.currentTimeMillis()
                )
            }
            .distinctBy { it.bssid }
            .sortedByDescending { it.rssi }
    }
}
