package com.pennapps.labs.pennmobile.compose.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private val lightColorScheme =
    lightColorScheme(
        primary = Color(0xFF12274B),
        secondary = Color(0xFF41c0ee),
        onPrimary = Color.White,
        onSecondary = Color.White,
        background = Color(0xFFFAFAFA),
        surfaceContainer = Color.White,
        surface = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onSurfaceVariant = AppColors.LabsDarkBlue,
        error = AppColors.BrightRed,
        onError = Color.White,
    )

private val darkColorScheme =
    darkColorScheme(
        primary = Color(0xFF12274B),
        secondary = Color(0xFF41c0ee),
        onPrimary = Color.White,
        onSecondary = Color.White,
        background = Color(0xFF000000),
        surfaceContainer = Color(0x0F030303),
        surface = AppColors.Gray,
        onSurfaceVariant = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        error = AppColors.BrightRed,
        onError = Color.White,
    )

@OptIn(ExperimentalMaterial3Api::class)
val rippleConfiguration =
    RippleConfiguration(
        color = AppColors.LabsDarkBlue,
        rippleAlpha = RippleAlpha(draggedAlpha = 0.2f, focusedAlpha = 0.2f, hoveredAlpha = 0.2f, pressedAlpha = 0.2f),
    )

@OptIn(ExperimentalMaterial3Api::class)
val pinkRippleConfiguration =
    RippleConfiguration(
        color = AppColors.BrightRed,
        rippleAlpha = RippleAlpha(draggedAlpha = 0.2f, focusedAlpha = 0.2f, hoveredAlpha = 0.2f, pressedAlpha = 0.2f),
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
    ) {
        content()
    }
}

/**
 * Same sizes and weights, every style in [fontFamily].
 *
 * Material components (chips, segmented buttons, dialogs) read their text style from the
 * theme typography, so setting a font on individual Text calls doesn't reach them.
 */
fun Typography.withFontFamily(fontFamily: FontFamily): Typography =
    Typography(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
    )
