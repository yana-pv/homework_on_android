package com.example.homework3.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    val title: String,
    val message: String,
    val isExpandable: Boolean,
    val priority: NotificationPriority,
    val shouldOpenApp: Boolean,
    val hasReplyAction: Boolean
) : Parcelable