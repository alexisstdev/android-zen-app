package com.example.zenapp.data.remote

import com.example.zenapp.data.remote.dto.*
import retrofit2.http.*

interface ZenAppApi {
    
    @GET("api/v1/users/{userId}/settings")
    suspend fun getUserSettings(
        @Path("userId") userId: String
    ): ApiResponse<UserSettingsDto>
    
    @POST("api/v1/users/{userId}/settings")
    suspend fun saveUserSettings(
        @Path("userId") userId: String,
        @Body settings: UserSettingsDto
    ): ApiResponse<UserSettingsDto>
    
    @PUT("api/v1/users/{userId}/settings")
    suspend fun updateUserSettings(
        @Path("userId") userId: String,
        @Body settings: UserSettingsDto
    ): ApiResponse<UserSettingsDto>
    
    @POST("api/v1/users/{userId}/usage/backup")
    suspend fun backupUsageData(
        @Path("userId") userId: String,
        @Body usageData: UsageBackupDto
    ): ApiResponse<UsageBackupDto>
    
    @GET("api/v1/mindful_prompts")
    suspend fun getMindfulPrompt(): ApiResponse<MindfulPromptDto>
    
    @GET("api/v1/mini_tasks/daily")
    suspend fun getDailyMiniTask(): ApiResponse<MiniTaskDto>
    
    @GET("api/v1/quotes/focus")
    suspend fun getFocusQuote(): ApiResponse<FocusQuoteDto>
    
    @GET("api/v1/health")
    suspend fun healthCheck(): ApiResponse<Map<String, String>>
}
