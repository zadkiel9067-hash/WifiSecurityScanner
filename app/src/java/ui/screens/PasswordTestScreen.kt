// فایل: app/src/main/java/com/example/wifiscanner/ui/screens/PasswordTestScreen.kt
package com.example.wifiscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifiscanner.utils.PasswordStrengthTester

@Composable
fun PasswordTestScreen() {
    var password by remember { mutableStateOf("") }
    val tester = remember { PasswordStrengthTester() }
    val result = tester.testPasswordStrength(password)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🔐 تست امنیت رمز وای‌فای",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("رمز عبور را وارد کن") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LinearProgressIndicator(
            progress = result.strengthScore / 100f,
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = when {
                result.strengthScore < 40 -> Color.Red
                result.strengthScore < 70 -> Color.Yellow
                else -> Color.Green
            }
        )
        
        Text(
            text = "امتیاز: ${result.strengthScore}/100",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
            Text(
                text = "⏱️ زمان تقریبی کرک: ${result.timeToCrack}",
                modifier = Modifier.padding(12.dp)
            )
        }
        
        if (result.suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("📋 پیشنهادات امنیتی:", fontWeight = FontWeight.Bold)
            result.suggestions.forEach { suggestion ->
                Text("• $suggestion", fontSize = 14.sp)
            }
        }
        
        if (result.isWeak && password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))) {
                Text(
                    text = "⚠️ هشدار امنیتی: این رمز بسیار ضعیف است!",
                    modifier = Modifier.padding(12.dp),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
