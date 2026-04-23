// فایل: app/src/main/java/com/example/wifiscanner/ui/components/SignalStrengthChart.kt
package com.example.wifiscanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wifiscanner.data.model.WifiInfo
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun SignalStrengthChart(
    networks: List<WifiInfo>,
    selectedNetwork: WifiInfo? = null,
    modifier: Modifier = Modifier,
    isLiveMode: Boolean = false
) {
    var liveData by remember { mutableStateOf<List<WifiInfo>>(networks) }
    var isAnimating by remember { mutableStateOf(false) }
    
    // شبیه‌سازی داده‌های زنده
    LaunchedEffect(isLiveMode) {
        if (isLiveMode) {
            isAnimating = true
            while (true) {
                delay(1000)
                // به‌روزرسانی داده‌ها با مقادیر جدید
                liveData = networks.map { network ->
                    network.copy(rssi = network.rssi + (Random.nextDouble(-3.0, 3.0)).toInt())
                }
            }
        }
    }
    
    val displayData = if (isLiveMode) liveData else networks
    val topNetworks = displayData.take(8).sortedByDescending { it.rssi }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // هدر نمودار
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📊 قدرت سیگنال شبکه‌ها",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                if (isLiveMode) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAnimating) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isAnimating) Color(0xFF4CAF50) else Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAnimating) "زنده" else "آفلاین",
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // نمودار خطی قدرت سیگنال
            if (topNetworks.isNotEmpty()) {
                LineChart(
                    networks = topNetworks,
                    height = 200,
                    selectedNetwork = selectedNetwork
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "شبکه‌ای برای نمایش وجود ندارد",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // راهنما
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                LegendItem(color = Color(0xFF2196F3), text = "قدرت سیگنال (dBm)")
                LegendItem(color = Color(0xFFFF9800), text = "میانگین")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // نوارهای قدرت سیگنال
            Text(
                text = "🔊 مقایسه قدرت سیگنال",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            topNetworks.take(5).forEach { network ->
                SignalBar(network = network)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun LineChart(
    networks: List<WifiInfo>,
    height: Int,
    selectedNetwork: WifiInfo? = null
) {
    if (networks.isEmpty()) return
    
    val topPadding = 20
    val bottomPadding = 30
    val chartHeight = height - topPadding - bottomPadding
    
    // محاسبه محدوده مقادیر RSSI
    val minRssi = networks.minOfOrNull { it.rssi } ?: -100
    val maxRssi = networks.maxOfOrNull { it.rssi } ?: -30
    val rssiRange = maxRssi - minRssi
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        val canvasWidth = size.width
        val stepX = canvasWidth / (networks.size - 1).coerceAtLeast(1)
        
        // رسم خطوط محور
        val axisPaint = Paint().apply {
            color = Color.LightGray.copy(alpha = 0.5f)
            strokeWidth = 1f
        }
        
        // رسم خطوط افقی راهنما
        for (i in 0..4) {
            val y = topPadding + (chartHeight * i / 4f)
            drawLine(
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            
            // نوشتن مقادیر dBm
            val rssiValue = maxRssi - (rssiRange * i / 4f)
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                drawText("${rssiValue.toInt()} dBm", canvasWidth - 5f, y + 8f, paint)
            }
        }
        
        // رسم محور Y
        drawLine(
            start = Offset(30f, topPadding.toFloat()),
            end = Offset(30f, (topPadding + chartHeight).toFloat()),
            strokeWidth = 2f,
            color = Color.Gray
        )
        
        // رسم نقاط و خطوط
        val points = mutableListOf<Offset>()
        networks.forEachIndexed { index, network ->
            val x = index * stepX
            val normalizedValue = ((network.rssi - minRssi) / rssiRange).coerceIn(0f, 1f)
            val y = topPadding + chartHeight - (normalizedValue * chartHeight)
            points.add(Offset(x, y))
        }
        
        // رسم خط اتصال
        if (points.size >= 2) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 3f,
                    color = Color(0xFF2196F3)
                )
            }
        }
        
        // رسم نقاط
        points.forEachIndexed { index, point ->
            val isSelected = selectedNetwork?.ssid == networks[index].ssid
            drawCircle(
                center = point,
                radius = if (isSelected) 8f else 5f,
                color = if (isSelected) Color(0xFFFF9800) else Color(0xFF2196F3)
            )
            
            // نوشتن نام شبکه زیر نقطه
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val name = if (networks[index].ssid.length > 10) {
                    networks[index].ssid.take(8) + "..."
                } else {
                    networks[index].ssid
                }
                drawText(name, point.x, (topPadding + chartHeight + 20).toFloat(), paint)
            }
        }
        
        // رسم خط میانگین
        val avgRssi = networks.map { it.rssi }.average()
        val avgNormalized = ((avgRssi - minRssi) / rssiRange).coerceIn(0f, 1f)
        val avgY = topPadding + chartHeight - (avgNormalized * chartHeight)
        
        drawLine(
            start = Offset(0f, avgY.toFloat()),
            end = Offset(canvasWidth, avgY.toFloat()),
            strokeWidth = 2f,
            color = Color(0xFFFF9800),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    }
}

@Composable
fun SignalBar(network: WifiInfo) {
    val signalPercent = ((network.rssi + 100) / 70f).coerceIn(0f, 1f)
    val barColor = when {
        network.rssi > -50 -> Color(0xFF4CAF50)
        network.rssi > -65 -> Color(0xFF8BC34A)
        network.rssi > -75 -> Color(0xFFFFC107)
        network.rssi > -85 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = network.ssid.take(20),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(120.dp)
            )
            Text(
                text = "${network.rssi} dBm",
                fontSize = 12.sp,
                color = barColor
            )
            Text(
                text = network.signalStrength,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // نوار سیگنال
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(signalPercent)
                    .fillMaxHeight()
                    .background(barColor, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun RealTimeSignalMeter(
    network: WifiInfo,
    modifier: Modifier = Modifier
) {
    var currentRssi by remember { mutableStateOf(network.rssi) }
    var isConnected by remember { mutableStateOf(false) }
    
    // شبیه‌سازی نوسانات سیگنال زنده
    LaunchedEffect(Unit) {
        isConnected = true
        while (isConnected) {
            delay(500)
            currentRssi = currentRssi + (Random.nextDouble(-2.0, 2.0)).toInt()
            currentRssi = currentRssi.coerceIn(-100, -30)
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A237E).copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📡 ${network.ssid}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // آنالوگ گیج دایره‌ای
            CircularSignalGauge(rssi = currentRssi)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${currentRssi} dBm",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = when {
                    currentRssi > -50 -> "کیفیت عالی 📶📶📶"
                    currentRssi > -65 -> "کیفیت خوب 📶📶"
                    currentRssi > -75 -> "کیفیت متوسط 📶"
                    else -> "کیفیت ضعیف 📶"
                },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CircularSignalGauge(rssi: Int) {
    val percentage = ((rssi + 100) / 70f).coerceIn(0f, 1f)
    val angle = percentage * 180f
    val color = when {
        rssi > -50 -> Color(0xFF4CAF50)
        rssi > -65 -> Color(0xFF8BC34A)
        rssi > -75 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(120.dp)
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            // پس‌زمینه
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 12f)
            )
            
            // میزان سیگنال
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                style = Stroke(width = 12f)
            )
        }
        
        // آیکون مرکزی
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Wifi,
            contentDescription = "Signal",
            tint = color,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun SignalHistoryChart(
    history: List<Pair<Long, Int>>, // زمان و RSSI
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) return
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "📈 تاریخچه سیگنال (آخرین ۱۰ ثانیه)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (history.size - 1).coerceAtLeast(1)
                
                val minRssi = history.minOfOrNull { it.second } ?: -100
                val maxRssi = history.maxOfOrNull { it.second } ?: -30
                val rssiRange = maxRssi - minRssi
                
                val points = history.mapIndexed { index, (_, rssi) ->
                    val x = index * stepX
                    val normalized = ((rssi - minRssi) / rssiRange).coerceIn(0f, 1f)
                    val y = height - (normalized * height)
                    Offset(x, y)
                }
                
                // رسم خط
                for (i in 0 until points.size - 1) {
                    drawLine(
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 3f,
                        color = Color(0xFF2196F3)
                    )
                }
                
                // رسم نقاط
                points.forEach { point ->
                    drawCircle(
                        center = point,
                        radius = 4f,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}
