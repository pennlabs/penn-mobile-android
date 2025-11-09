package com.pennapps.labs.pennmobile.compose.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColorScheme = lightColorScheme(
    primary = Color(0xFF12274B),
    secondary = Color(0xFF41c0ee),

    onPrimary = Color.White,
    onSecondary = Color.White,

    background = Color(0xFFFAFAFA),
    surfaceContainer = Color.White,
    surface = Color.White,

    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = AppColors.TabTextBlue,

    error = AppColors.BrightRed,
    onError = Color.White
)

private val darkColorScheme = darkColorScheme(
    primary = Color(0xFF12274B),
    secondary = Color(0xFF41c0ee),

    onPrimary = Color.White,
    onSecondary = Color.White,

    background = Color(0x0F000000),

    surfaceContainer = Color(0x0F030303),
    surface = AppColors.Gray,
    onSurfaceVariant = Color.White,

    onBackground = Color.White,
    onSurface = Color.White,

    error = AppColors.BrightRed,
    onError = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
val rippleConfiguration = RippleConfiguration(
    color = AppColors.TabTextBlue,
    rippleAlpha = RippleAlpha(draggedAlpha = 0.2f, focusedAlpha = 0.2f, hoveredAlpha = 0.2f, pressedAlpha = 0.2f)
)

@OptIn(ExperimentalMaterial3Api::class)
val pinkRippleConfiguration = RippleConfiguration(
    color = AppColors.BrightRed,
    rippleAlpha = RippleAlpha(draggedAlpha = 0.2f, focusedAlpha = 0.2f, hoveredAlpha = 0.2f, pressedAlpha = 0.2f)
)


@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme
    ) {
        content()
    }
}