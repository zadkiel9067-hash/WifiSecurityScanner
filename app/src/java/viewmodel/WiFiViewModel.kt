// فایل: app/src/main/java/com/example/wifiscanner/viewmodel/WiFiViewModel.kt
package com.example.wifiscanner.viewmodel

import android.app.Application
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifiscanner.data.model.WifiInfo
import com.example.wifiscanner.data.model.EvilTwinResult
import com.example.wifiscanner.data.repository.WifiRepository
import com.example.wifiscanner.utils.EvilTwinDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class WiFiUiState(
    val networks: List<WifiInfo> = emptyList(),
    val evilTwins: List<EvilTwinResult> = emptyList(),
    val isScanning: Boolean = false,
    val lastScanTime: Long = 0,
    val error: String? = null,
    val scanCount: Int = 0
)

class WiFiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WifiRepository(application)
    private val evilTwinDetector = EvilTwinDetector()
    
    private val _uiState = MutableStateFlow(WiFiUiState())
    val uiState: StateFlow<WiFiUiState> = _uiState
    
    fun scanNetworks(wifiManager: WifiManager) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, error = null)
            
            delay(500)
            
            try {
                val networks = withContext(Dispatchers.IO) {
                    repository.scanNetworks()
                }
                
                val evilTwins = evilTwinDetector.detectEvilTwins(networks)
                
                _uiState.value = WiFiUiState(
                    networks = networks,
                    evilTwins = evilTwins,
                    isScanning = false,
                    lastScanTime = System.currentTimeMillis(),
                    scanCount = _uiState.value.scanCount + 1
                )
                
                if (networks.isNotEmpty()) {
                    repository.saveScanResults(networks)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "خطا: ${e.message}",
                    isScanning = false
                )
            }
        }
    }
    
    suspend fun getHistory(): List<WifiInfo> {
        return repository.getAllScans().firstOrNull() ?: emptyList()
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
