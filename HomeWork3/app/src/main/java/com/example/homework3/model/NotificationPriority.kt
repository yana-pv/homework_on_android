package com.example.homework3.model

import androidx.annotation.StringRes
import com.example.homework3.R

enum class NotificationPriority(val importance: Int, @StringRes val displayNameRes: Int) {
    MIN(1, R.string.priority_min),
    LOW(2, R.string.priority_low),
    MEDIUM(3, R.string.priority_medium),
    HIGH(4, R.string.priority_high)
}