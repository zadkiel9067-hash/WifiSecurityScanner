// فایل: app/src/main/java/com/example/wifiscanner/data/local/WifiDatabase.kt
package com.example.wifiscanner.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.wifiscanner.data.model.WifiInfo

@Database(entities = [WifiInfo::class], version = 1, exportSchema = false)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun wifiDao(): WifiDao
    
    companion object {
        @Volatile
        private var INSTANCE: WifiDatabase? = null
        
        fun getDatabase(context: Context): WifiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WifiDatabase::class.java,
                    "wifi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
