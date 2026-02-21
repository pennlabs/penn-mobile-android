package com.pennapps.labs.pennmobile.data_classes

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class HomeSlidingToolbarElement(
        @DrawableRes val iconRes: Int,
        val title: String,
)
