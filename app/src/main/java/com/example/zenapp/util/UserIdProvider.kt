package com.example.zenapp.util

import android.content.Context
import android.provider.Settings

object UserIdProvider {
    fun getUserId(context: Context): String {
        // Usa Android ID como identificador único del dispositivo
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "default_user"
    }
}
