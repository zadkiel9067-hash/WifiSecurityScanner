// فایل: app/src/main/java/com/example/wifiscanner/ui/components/EvilTwinCard.kt
package com.example.wifiscanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifiscanner.data.model.EvilTwinResult
import com.example.wifiscanner.data.model.RiskLevel

@Composable
fun EvilTwinCard(result: EvilTwinResult) {
    val (cardColor, icon, iconTint) = when (result.riskLevel) {
        RiskLevel.CRITICAL -> Triple(
            Color(0xFFFFCDD2),      // قرمز روشن
            Icons.Default.Error,
            Color.Red
        )
        RiskLevel.HIGH -> Triple(
            Color(0xFFFFE0B2),       // نارنجی روشن
            Icons.Default.Warning,
            Color(0xFFFF9800)
        )
        RiskLevel.MEDIUM -> Triple(
            Color(0xFFFFF9C4),       // زرد روشن
            Icons.Default.Warning,
            Color(0xFFF57C00)
        )
        else -> Triple(
            Color(0xFFC8E6C9),       // سبز روشن
            Icons.Default.Info,
            Color(0xFF4CAF50)
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // هدر با آیکون و عنوان
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        contentDescription = "Warning",
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "⚠️ هشدار امنیتی",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconTint
                        )
                        Text(
                            text = result.ssid,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                
                // نشانگر سطح خطر
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = iconTint.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = when (result.riskLevel) {
                            RiskLevel.CRITICAL -> "بسیار خطرناک"
                            RiskLevel.HIGH -> "خطرناک"
                            RiskLevel.MEDIUM -> "متوسط"
                            else -> "امن"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // خط جداکننده
            Divider(color = iconTint.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // توضیحات
            Text(
                text = result.suggestion,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            
            // نمایش BSSIDهای جعلی
            if (result.fakeBSSIDs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🔍 شبکه‌های جعلی شناسایی شده: ${result.fakeBSSIDs.size} عدد",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                // نمایش حداکثر 3 BSSID اول
                result.fakeBSSIDs.take(3).forEach { bssid ->
                    Text(
                        text = "  • $bssid",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                if (result.fakeBSSIDs.size > 3) {
                    Text(
                        text = "  و ${result.fakeBSSIDs.size - 3} عدد دیگر...",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            // هشدار جدی برای سطح CRITICAL
            if (result.riskLevel == RiskLevel.CRITICAL) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "⚠️ به این شبکه هرگز وصل نشوید! این یک شبکه جعلی است.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            // توصیه امنیتی
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 ${getSecurityTip(result.riskLevel)}",
                fontSize = 11.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

private fun getSecurityTip(riskLevel: RiskLevel): String {
    return when (riskLevel) {
        RiskLevel.CRITICAL -> "فوراً از این شبکه دور شوید و به شبکه اصلی با رمز قوی متصل شوید"
        RiskLevel.HIGH -> "از اتصال به شبکه‌های ناشناس با نام مشابه خودداری کنید"
        RiskLevel.MEDIUM -> "قبل از اتصال، از صحت MAC address شبکه اطمینان حاصل کنید"
        else -> "شبکه به نظر امن می‌رسد، اما همیشه مراقب باشید"
    }
}
