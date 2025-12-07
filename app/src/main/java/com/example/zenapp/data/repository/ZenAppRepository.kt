package com.example.zenapp.data.repository

import android.content.Context
import com.example.zenapp.data.local.ZenAppDatabase
import com.example.zenapp.data.local.entity.BlockedAppEntity
import com.example.zenapp.data.local.entity.SettingsEntity
import com.example.zenapp.data.remote.RetrofitInstance
import com.example.zenapp.data.remote.dto.UserSettingsDto
import com.example.zenapp.data.remote.dto.UsageBackupDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class ZenAppRepository(private val context: Context) {
    
    private val api = RetrofitInstance.api
    private val database = ZenAppDatabase.getDatabase(context)
    private val blockedAppDao = database.blockedAppDao()
    private val settingsDao = database.settingsDao()
    
    suspend fun getUserSettings(userId: String): Result<UserSettingsDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserSettings(userId)
                if (response.success && response.data != null) {
                    // Guardar en Room para cache offline
                    cacheSettings(response.data)
                    Result.Success(response.data)
                } else {
                    // Intentar cargar desde cache
                    loadFromCache() ?: Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                // Si falla la red, cargar desde cache
                loadFromCache() ?: Result.Error(e.message ?: "Network error")
            }
        }
    }
    
    suspend fun saveUserSettings(userId: String, settings: UserSettingsDto): Result<UserSettingsDto> {
        return withContext(Dispatchers.IO) {
            try {
                // Primero guardar en cache local
                cacheSettings(settings)
                
                // Luego intentar sincronizar con servidor
                val response = api.saveUserSettings(userId, settings)
                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                // Aunque falle la red, ya está guardado localmente
                Result.Success(settings)
            }
        }
    }
    
    private suspend fun cacheSettings(settings: UserSettingsDto) {
        android.util.Log.d("ZenAppRepository", "cacheSettings: Saving ${settings.selectedApps.size} apps")
        android.util.Log.d("ZenAppRepository", "cacheSettings: pauseTime=${settings.pauseTime}, message=${settings.customMessage}")
        
        // Guardar apps bloqueadas con nombre real
        val pm = context.packageManager
        val blockedApps = settings.selectedApps.map { packageName ->
            val appName = try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                pm.getApplicationLabel(appInfo).toString()
            } catch (e: Exception) {
                packageName.split(".").lastOrNull() ?: packageName
            }
            
            BlockedAppEntity(
                packageName = packageName,
                appName = appName,
                isBlocked = true
            )
        }
        blockedAppDao.replaceAll(blockedApps)
        android.util.Log.d("ZenAppRepository", "cacheSettings: Saved ${blockedApps.size} apps to Room")
        
        // Guardar configuración
        val settingsEntity = SettingsEntity(
            id = 1,
            pauseTime = settings.pauseTime,
            customMessage = settings.customMessage,
            dailyOpens = settings.dailyOpens,
            sessionDuration = settings.sessionDuration
        )
        settingsDao.insertSettings(settingsEntity)
        android.util.Log.d("ZenAppRepository", "cacheSettings: Settings saved to Room")
    }
    
    private suspend fun loadFromCache(): Result<UserSettingsDto>? {
        val settings = settingsDao.getSettingsOnce() ?: return null
        val apps = blockedAppDao.getAllBlockedApps().first()
        
        return Result.Success(
            UserSettingsDto(
                selectedApps = apps.map { it.packageName },
                pauseTime = settings.pauseTime,
                customMessage = settings.customMessage,
                dailyOpens = settings.dailyOpens,
                sessionDuration = settings.sessionDuration
            )
        )
    }
    
    suspend fun backupUsageData(userId: String, usageData: UsageBackupDto): Result<UsageBackupDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.backupUsageData(userId, usageData)
                if (response.success && response.data != null) {
                    Result.Success(response.data)
                } else {
                    Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }
    }
    
    suspend fun getMindfulPrompt(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getMindfulPrompt()
                if (response.success && response.data != null) {
                    Result.Success(response.data.prompt)
                } else {
                    Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }
    }
    
    suspend fun getDailyMiniTask(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getDailyMiniTask()
                if (response.success && response.data != null) {
                    Result.Success(response.data.task)
                } else {
                    Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }
    }
    
    suspend fun getFocusQuote(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getFocusQuote()
                if (response.success && response.data != null) {
                    Result.Success(response.data.quote)
                } else {
                    Result.Error(response.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error")
            }
        }
    }
}
