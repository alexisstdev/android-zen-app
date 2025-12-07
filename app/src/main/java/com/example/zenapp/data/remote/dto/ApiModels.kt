package com.example.zenapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class UserSettingsDto(
    val selectedApps: List<String> = emptyList(),
    val customMessage: String = "",
    val pauseTime: String = "",
    val dailyOpens: String = "",
    val sessionDuration: String = ""
)

@JsonClass(generateAdapter = true)
data class UsageBackupDto(
    val timestamp: String? = null,
    val totalScreenTime: Long = 0,
    val appsUsed: Map<String, Long> = emptyMap(),
    val blocksTriggered: Int = 0
)

@JsonClass(generateAdapter = true)
data class MindfulPromptDto(
    val prompt: String
)

@JsonClass(generateAdapter = true)
data class MiniTaskDto(
    val task: String
)

@JsonClass(generateAdapter = true)
data class FocusQuoteDto(
    val quote: String
)
