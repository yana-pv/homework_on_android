package com.example.homework5.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.homework5.R
import com.example.homework5.constants.EventCategory
import com.example.homework5.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

fun EventCategory.getDisplayName(context: Context): String {
    return when (this) {
        EventCategory.PERSONAL -> context.getString(R.string.category_personal)
        EventCategory.WORK -> context.getString(R.string.category_work)
        EventCategory.ENTERTAINMENT -> context.getString(R.string.category_entertainment)
        EventCategory.HEALTH -> context.getString(R.string.category_health)
        EventCategory.LEARNING -> context.getString(R.string.category_learning)
        EventCategory.OTHER -> context.getString(R.string.category_other)
    }
}

fun EventCategory.getColor(): Color {
    return when (this) {
        EventCategory.PERSONAL -> Blue40
        EventCategory.WORK -> WorkGreen40
        EventCategory.ENTERTAINMENT -> Orange40
        EventCategory.HEALTH -> Red40
        EventCategory.LEARNING -> Purple40
        EventCategory.OTHER -> GreyCategory
    }
}

fun Date.formatDate(): String {
    val locale = Locale("ru", "RU")
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", locale)
    return formatter.format(this)
}