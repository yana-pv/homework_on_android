package com.example.homework3.model

import java.util.Date

data class UserMessage(
    val text: String,
    val timestamp: Date = Date(),
    val isFromReply: Boolean = false
)