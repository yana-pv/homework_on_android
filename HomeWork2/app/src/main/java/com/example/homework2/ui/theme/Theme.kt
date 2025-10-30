package com.example.homework2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.example.homework2.data.ColorSchemes

object AppTheme {
    var currentColorScheme = mutableStateOf(ColorSchemes.Blue)
}

@Composable
fun NotesAppTheme(
    content: @Composable () -> Unit
) {
    val currentScheme = AppTheme.currentColorScheme.value

    val colorScheme = lightColorScheme(
        primary = currentScheme.primaryColor,
        onPrimary = Color.White,
        background = currentScheme.backgroundColor,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}