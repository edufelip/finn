package com.edufelip.finn.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2962FF),
    secondary = Color(0xFF00BCD4),
    tertiary = Color(0xFF7C4DFF),
    background = Color(0xFFF7F9FC),
    surface = Color.White,
    error = Color(0xFFB00020),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF82B1FF),
    secondary = Color(0xFF4DD0E1),
    tertiary = Color(0xFFB388FF),
    background = Color(0xFF0E1116),
    surface = Color(0xFF1A1E24),
    error = Color(0xFFCF6679),
)

private val AppTypography = Typography()

@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
