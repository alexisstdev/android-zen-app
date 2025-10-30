package com.example.zenapp.ui.appselection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AppCategory(val displayName: String) : Parcelable {
    SOCIAL("Redes Sociales"),
    GAMES("Juegos"),
    ENTERTAINMENT("Entretenimiento"),
    PRODUCTIVITY("Productividad"),
    OTHER("Otras")
}

