package com.pennapps.labs.pennmobile.components.sneaker

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat

internal object Utils {
    /**
     * Returns status bar height.
     *
     * @return
     */
    fun getStatusBarHeight(activityDecorView: ViewGroup?): Int {
        return if (activityDecorView != null) {
            val rectangle = Rect()
            activityDecorView.getWindowVisibleDisplayFrame(rectangle)
            rectangle.top
        } else {
            0
        }
    }

    fun convertToDp(
        context: Context,
        sizeInDp: Float,
    ): Int {
        val scale = context.resources.displayMetrics.density
        return (sizeInDp * scale + 0.5f).toInt()
    }

    fun customView(
        context: Context,
        backgroundColor: Int,
        cornerRadius: Int,
    ): GradientDrawable {
        val radiusInDP = convertToDp(context, cornerRadius.toFloat())
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii =
                floatArrayOf(
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                    radiusInDP.toFloat(),
                )
            setColor(backgroundColor)
        }
    }

    fun getColor(
        context: Context,
        color: Int,
    ): Int {
        return try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
    }
}
