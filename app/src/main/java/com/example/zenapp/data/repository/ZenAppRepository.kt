package com.example.zenapp.data.repository

import com.example.zenapp.data.remote.RetrofitInstance
import com.example.zenapp.data.remote.dto.UserSettingsDto
import com.example.zenapp.data.remote.dto.UsageBackupDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class ZenAppRepository {
    
    private val api = RetrofitInstance.api
    
    suspend fun getUserSettings(userId: String): Result<UserSettingsDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserSettings(userId)
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
    
    suspend fun saveUserSettings(userId: String, settings: UserSettingsDto): Result<UserSettingsDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.saveUserSettings(userId, settings)
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
