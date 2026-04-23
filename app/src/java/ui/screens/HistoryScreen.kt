// فایل: app/src/main/java/com/example/wifiscanner/ui/screens/HistoryScreen.kt
package com.example.wifiscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wifiscanner.viewmodel.WiFiViewModel
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(viewModel: WiFiViewModel) {
    val scope = rememberCoroutineScope()
    var history by remember { mutableStateOf(emptyList<com.example.wifiscanner.data.model.WifiInfo>()) }
    
    LaunchedEffect(Unit) {
        scope.launch {
            history = viewModel.getHistory()
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "📜 تاریخچه اسکن‌ها",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        
        if (history.isEmpty()) {
            Text(
                text = "هنوز اسکنی انجام نشده",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(history) { scan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(scan.ssid, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("BSSID: ${scan.bssid}")
                            Text("Signal: ${scan.rssi} dBm")
                            Text("زمان: ${android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", scan.timestamp)}")
                        }
                    }
                }
            }
        }
    }
}
