package com.example.zenapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zenapp.data.local.dao.BlockedAppDao
import com.example.zenapp.data.local.dao.SettingsDao
import com.example.zenapp.data.local.entity.BlockedAppEntity
import com.example.zenapp.data.local.entity.SettingsEntity

@Database(
    entities = [BlockedAppEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ZenAppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun settingsDao(): SettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: ZenAppDatabase? = null
        
        fun getDatabase(context: Context): ZenAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZenAppDatabase::class.java,
                    "zen_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
