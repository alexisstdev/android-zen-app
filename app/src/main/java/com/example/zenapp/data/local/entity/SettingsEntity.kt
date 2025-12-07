package com.example.zenapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val pauseTime: String = "30 segundos",
    val customMessage: String = "Mantén el enfoque. Esta app estará disponible en un momento.",
    val dailyOpens: String = "3",
    val sessionDuration: String = "10 minutos"
)
