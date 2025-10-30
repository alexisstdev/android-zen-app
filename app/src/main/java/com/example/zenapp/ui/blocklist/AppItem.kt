package com.example.zenapp.ui.blocklist

import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.example.zenapp.ui.appselection.AppCategory
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppItem(
    val name: String,
    val packageName: String,
    val category: AppCategory,
    var isBlocked: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    var iconDrawable: Drawable? = null
}

