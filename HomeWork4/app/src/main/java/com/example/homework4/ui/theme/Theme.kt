package com.example.homework4.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = GreenBackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnGreenLight,
    onSecondary = OnGreenLight,
    onTertiary = OnGreenLight,
    onBackground = OnBackgroundLight,
    onSurface = OnBackgroundLight,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenTertiary,
    secondary = GreenSecondary,
    tertiary = GreenLight,
    background = GreenBackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnGreenDark,
    onSecondary = OnGreenDark,
    onTertiary = OnGreenDark,
    onBackground = OnBackgroundDark,
    onSurface = OnBackgroundDark,
    primaryContainer = GreenPrimary,
    onPrimaryContainer = OnBackgroundDark,
)

@Composable
fun HomeWork4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}