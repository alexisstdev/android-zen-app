package com.example.zenapp.data.local.dao

import androidx.room.*
import com.example.zenapp.data.local.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>
    
    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getBlockedApp(packageName: String): BlockedAppEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<BlockedAppEntity>)
    
    @Query("DELETE FROM blocked_apps")
    suspend fun deleteAll()
    
    @Transaction
    suspend fun replaceAll(apps: List<BlockedAppEntity>) {
        deleteAll()
        insertAll(apps)
    }
}
