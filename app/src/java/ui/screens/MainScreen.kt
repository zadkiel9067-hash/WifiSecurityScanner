// فایل: app/src/main/java/com/example/wifiscanner/ui/screens/MainScreen.kt
package com.example.wifiscanner.ui.screens

import android.net.wifi.WifiManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifiscanner.data.model.WifiInfo
import com.example.wifiscanner.data.model.EvilTwinResult
import com.example.wifiscanner.data.model.RiskLevel
import com.example.wifiscanner.viewmodel.WiFiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: WiFiViewModel, wifiManager: WifiManager) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📡 Wifi Security Scanner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.scanNetworks(wifiManager) },
                containerColor = if (uiState.isScanning) Color.Gray else MaterialTheme.colorScheme.primary
            ) {
                if (uiState.isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Scan")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.error != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))) {
                        Text(
                            text = "❌ ${uiState.error}",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }
                }
            }
            
            if (uiState.isScanning) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("در حال اسکن شبکه‌ها...", modifier = Modifier.fillMaxWidth())
                }
            }
            
            if (uiState.evilTwins.isNotEmpty()) {
                item {
                    Text(
                        text = "⚠️ هشدارهای امنیتی",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
                items(uiState.evilTwins) { evilTwin ->
                    EvilTwinWarningCard(evilTwin)
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📶 شبکه‌ها: ${uiState.networks.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (uiState.lastScanTime > 0) {
                        Text(
                            text = "آخرین اسکن: ${android.text.format.DateFormat.format("HH:mm:ss", uiState.lastScanTime)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            items(uiState.networks) { network ->
                WiFiCard(network = network)
            }
            
            if (uiState.networks.isEmpty() && !uiState.isScanning) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Text(
                            text = "🔍 دکمه اسکن را بزنید تا شبکه‌های وای‌فای را ببیند",
                            modifier = Modifier.padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WiFiCard(network: WifiInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = network.ssid,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = network.signalStrength,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = network.signalLevel / 4f,
                modifier = Modifier.fillMaxWidth(),
                color = when (network.signalLevel) {
                    4 -> Color.Green
                    3 -> Color(0xFF8BC34A)
                    2 -> Color.Yellow
                    else -> Color.Red
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "BSSID: ${network.bssid.takeLast(17)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Text(
                text = "امنیت: ${network.securityType}",
                fontSize = 12.sp,
                color = if (network.isSecure) Color(0xFF4CAF50) else Color(0xFFFF5722)
            )
            
            Text(
                text = "قدرت سیگنال: ${network.rssi} dBm",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun EvilTwinWarningCard(result: EvilTwinResult) {
    val cardColor = when (result.riskLevel) {
        RiskLevel.CRITICAL -> Color(0xFFFFCDD2)
        RiskLevel.HIGH -> Color(0xFFFFE0B2)
        RiskLevel.MEDIUM -> Color(0xFFFFF9C4)
        else -> Color(0xFFC8E6C9)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (result.riskLevel == RiskLevel.CRITICAL) Color.Red else Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚠️ ${result.ssid}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = result.suggestion,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (result.riskLevel == RiskLevel.CRITICAL) Color.Red else Color(0xFFD32F2F)
            )
        }
    }
}
