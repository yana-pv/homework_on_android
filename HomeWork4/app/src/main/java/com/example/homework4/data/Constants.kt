package com.example.homework4.data

import androidx.compose.ui.unit.dp

object Constants {
    // Slider
    const val SLIDER_MIN = 10
    const val SLIDER_MAX = 100
    const val SLIDER_STEP = 5

    // Coroutine delays
    const val MIN_DELAY_MS = 1000L
    const val MAX_DELAY_MS = 10000L
    const val SLOW_THRESHOLD_MS = 7000L
    const val EXCEPTION_PROBABILITY = 0.3f

    // Dispatchers
    const val DISPATCHER_DEFAULT = "Dispatchers.Default"
    const val DISPATCHER_IO = "Dispatchers.IO"
    const val DISPATCHER_MAIN = "Dispatchers.Main"
    const val DISPATCHER_UNCONFINED = "Dispatchers.Unconfined"

    // Exception types
    const val EXCEPTION_SLOW_OPERATION = "SlowOperationException"
    const val EXCEPTION_CRITICAL_ERROR = "CriticalErrorException"
    const val EXCEPTION_UNEXPECTED_STATE = "UnexpectedStateException"

    // UI Dimensions
    object Dimens {
        val SCREEN_PADDING = 16.dp
        val SMALL_SPACER = 8.dp
        val MEDIUM_SPACER = 16.dp
    }
}