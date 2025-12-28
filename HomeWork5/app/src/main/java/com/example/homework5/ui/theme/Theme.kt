package com.example.homework5.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green95,
    onPrimaryContainer = Green20,

    secondary = Grey40,
    onSecondary = Color.White,
    secondaryContainer = Grey95,
    onSecondaryContainer = Grey30,

    tertiary = Mint40,
    onTertiary = Grey10,

    background = Grey99,
    onBackground = Grey10,

    surface = Color.White,
    onSurface = Grey10,

    surfaceVariant = Grey95,
    onSurfaceVariant = Grey40,

    outline = Grey80,
    outlineVariant = Grey90,

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    scrim = Grey30.copy(alpha = 0.32f),
    inverseOnSurface = Grey95,
    inverseSurface = Grey20,
    inversePrimary = Green80
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Grey10,
    primaryContainer = Green30,
    onPrimaryContainer = Green95,

    secondary = Grey80,
    onSecondary = Grey10,
    secondaryContainer = Grey30,
    onSecondaryContainer = Grey90,

    tertiary = Teal40,
    onTertiary = Color.White,

    background = Grey10,
    onBackground = Grey90,

    surface = Grey20,
    onSurface = Grey90,

    surfaceVariant = Grey30,
    onSurfaceVariant = Grey80,

    outline = Grey40,
    outlineVariant = Grey30,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    scrim = Color.Black.copy(alpha = 0.5f),
    inverseOnSurface = Grey20,
    inverseSurface = Grey90,
    inversePrimary = Green40
)

@Composable
fun Homework5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.navigationBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}