// فایل: app/src/main/java/com/example/wifiscanner/service/BackgroundScanner.kt
package com.example.wifiscanner.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BackgroundScanner : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, NotificationCompat.Builder(this, "wifi_scanner_channel")
            .setContentTitle("Wifi Security Scanner")
            .setContentText("اسکن در حال اجراست...")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .build())
        
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
    }
}
