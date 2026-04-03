package com.pennapps.labs.pennmobile.compose.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Object to hold custom TextStyles for consistent typography across the app.
 */
object CustomTextStyles {
    /**
     * Text style for all headers in the dining hall screen
     */
    @Composable
    fun DiningHallsHeader(): TextStyle =
        TextStyle(
            fontFamily = GilroyFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
        )
}
