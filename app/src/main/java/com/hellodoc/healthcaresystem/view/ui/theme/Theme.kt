package com.hellodoc.healthcaresystem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HelloDocYellow,
    onPrimary = Color.Black,
    primaryContainer = BoxGrey,
    onPrimaryContainer = Color.White,
    background = Color(0xFF121212), // Deep black for gold/yellow contrast
    onBackground = Color.White,
    secondary = HelloDocYellow.copy(alpha = 0.7f),
    secondaryContainer = BoxLightGrey,
    onSecondaryContainer = Color.White,
    tertiary = AmberCustom,
    tertiaryContainer = LightDarkTheme,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = Color(0xFFCF6679),
)

private val LightColorScheme = lightColorScheme(
    primary = HelloDocYellow,
    onPrimary = Color.Black,
    primaryContainer = HelloDocYellow,
    onPrimaryContainer = Color.Black,
    background = Color(0xFFFFFDF0), // Very slight yellow tint background for warmth
    onBackground = Color.Black,
    secondary = Color(0xFF37474F), // Deep slate for contrast
    secondaryContainer = secondContainer,
    onSecondaryContainer = Color.DarkGray,
    tertiary = AmberCustom,
    tertiaryContainer = HelloDocYellow.copy(alpha = 0.2f),
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
)

@Composable
fun HealthCareSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}