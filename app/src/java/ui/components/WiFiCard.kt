// فایل: app/src/main/java/com/example/wifiscanner/ui/components/WiFiCard.kt
package com.example.wifiscanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifiscanner.data.model.WifiInfo

@Composable
fun WiFiCard(
    network: WifiInfo,
    onClick: (() -> Unit)? = null
) {
    val securityColor = if (network.isSecure) Color(0xFF4CAF50) else Color(0xFFFF5722)
    val signalColor = when (network.signalLevel) {
        4 -> Color(0xFF4CAF50)  // سبز
        3 -> Color(0xFF8BC34A)  // سبز روشن
        2 -> Color(0xFFFFC107)  // زرد
        1 -> Color(0xFFFF9800)  // نارنجی
        else -> Color(0xFFF44336) // قرمز
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .then(if (onClick != null) Modifier else Modifier),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ردیف اول: نام شبکه و قدرت سیگنال
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Wifi,
                        contentDescription = "WiFi",
                        tint = signalColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = network.ssif (network.ssid.length > 25) "${network.ssid.take(22)}..." else network.ssid,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                
                // نشانگر قدرت سیگنال
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(4) { index ->
                        Icon(
                            Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (index < network.signalLevel) signalColor else Color.LightGray.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${network.rssi} dBm",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // نوار پیشرفت سیگنال
            LinearProgressIndicator(
                progress = (network.rssi + 100) / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = signalColor,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // ردیف دوم: اطلاعات امنیت و BSSID
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (network.isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Security",
                        tint = securityColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = network.securityType,
                        fontSize = 12.sp,
                        color = securityColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = "BSSID",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = network.bssid.takeLast(17),
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
            
            // ردیف سوم: کیفیت اتصال
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (network.signalLevel) {
                    4 -> Color(0xFFE8F5E9)
                    3 -> Color(0xFFF1F8E9)
                    2 -> Color(0xFFFFFDE7)
                    else -> Color(0xFFFFEBEE)
                }
            ) {
                Text(
                    text = when (network.signalLevel) {
                        4 -> "📶 اتصال عالی"
                        3 -> "📶 اتصال خوب"
                        2 -> "📶 اتصال متوسط"
                        1 -> "📶 اتصال ضعیف"
                        else -> "📶 اتصال بسیار ضعیف"
                    },
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = when (network.signalLevel) {
                        4 -> Color(0xFF2E7D32)
                        3 -> Color(0xFF558B2F)
                        2 -> Color(0xFFF57F17)
                        else -> Color(0xFFC62828)
                    }
                )
            }
        }
    }
}
