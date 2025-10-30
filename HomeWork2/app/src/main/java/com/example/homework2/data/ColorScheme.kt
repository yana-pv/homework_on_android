package com.example.homework2.data

import androidx.compose.ui.graphics.Color
import com.example.homework2.R

data class AppColorScheme(
    val nameRes: Int,
    val primaryColor: Color,
    val backgroundColor: Color
)


object ColorSchemes {
    val Blue = AppColorScheme(
        nameRes = R.string.color_scheme_blue,
        primaryColor = Color(0xFF1976D2),
        backgroundColor = Color(0xFFE3F2FD)
    )

    val Red = AppColorScheme(
        nameRes = R.string.color_scheme_red,
        primaryColor = Color(0xFFD32F2F),
        backgroundColor = Color(0xFFFFEBEE)
    )

    val Green = AppColorScheme(
        nameRes = R.string.color_scheme_green,
        primaryColor = Color(0xFF388E3C),
        backgroundColor = Color(0xFFE8F5E8)
    )

    val Purple = AppColorScheme(
        nameRes = R.string.color_scheme_purple,
        primaryColor = Color(0xFF7B1FA2),
        backgroundColor = Color(0xFFF3E5F5)
    )

    val allSchemes = listOf(Blue, Red, Green, Purple)
}