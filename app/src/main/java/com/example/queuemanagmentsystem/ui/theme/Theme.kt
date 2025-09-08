package com.example.queuemanagmentsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand colors (People's Bank)
private val BrandRed = Color(0xFFC62828)
private val BrandRedDark = Color(0xFFEF5350) // slightly lighter in dark for contrast
private val BrandYellow = Color(0xFFF9A825)
private val BrandYellowDark = Color(0xFFFFC107)
private val BrandBackgroundLight = Color(0xFFFEFCF8)
private val BrandSurfaceLight = Color(0xFFFFFFFF)
private val BrandBackgroundDark = Color(0xFF121212)
private val BrandSurfaceDark = Color(0xFF1E1E1E)

private val DarkColorScheme = darkColorScheme(
    primary = BrandRedDark,
    onPrimary = Color.White,
    secondary = BrandYellowDark,
    onSecondary = Color.Black,
    tertiary = BrandYellowDark,
    background = BrandBackgroundDark,
    onBackground = Color(0xFFECECEC),
    surface = BrandSurfaceDark,
    onSurface = Color(0xFFECECEC),
    error = Color(0xFFEF5350),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    secondary = BrandYellow,
    onSecondary = Color(0xFF212121),
    tertiary = BrandYellow,
    background = BrandBackgroundLight,
    onBackground = Color(0xFF212121),
    surface = BrandSurfaceLight,
    onSurface = Color(0xFF212121),
    error = BrandRed,
    onError = Color.White
)

@Composable
fun QueueManagmentSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}