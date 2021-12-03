package com.pennapps.labs.pennmobile.components.floatingbottombar.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat

object DrawableHelper {
    internal fun createShapeDrawable(@ColorInt activeColor: Int,
                                     @FloatRange(from = 0.0) cornerRadius: Float,
                                     @FloatRange(from = 0.0, to = 1.0) opacity: Float): Drawable {
        val footerBackground = ShapeDrawable()

        val radii = FloatArray(81)
        for (i in 0 until 8) radii[i] = cornerRadius

        footerBackground.shape = RoundRectShape(radii, null, null)
        footerBackground.paint.color = ColorUtils.setAlphaComponent(activeColor, (opacity * 255).toInt())

        return footerBackground
    }

    internal fun createDrawable(context: Context,
                                @DrawableRes menuItem: Int,
                                stateList: ColorStateList): Drawable {
        val iconDrawable = ContextCompat.getDrawable(context, menuItem)!!
        DrawableCompat.setTintList(iconDrawable, stateList)
        return iconDrawable
    }
}
