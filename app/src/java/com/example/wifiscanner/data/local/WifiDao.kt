// فایل: app/src/main/java/com/example/wifiscanner/data/local/WifiDao.kt
package com.example.wifiscanner.data.local

import androidx.room.*
import com.example.wifiscanner.data.model.WifiInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: WifiInfo)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scans: List<WifiInfo>)
    
    @Query("SELECT * FROM wifi_networks ORDER BY timestamp DESC LIMIT 100")
    fun getAllScans(): Flow<List<WifiInfo>>
    
    @Query("DELETE FROM wifi_networks WHERE timestamp < :cutoffDate")
    suspend fun deleteOldScans(cutoffDate: Long)
    
    @Query("DELETE FROM wifi_networks")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM wifi_networks")
    suspend fun getCount(): Int
}
