package com.pennapps.labs.pennmobile.components.customloadingicon

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.pennapps.labs.pennmobile.R

/**
 * Davies Lumumba - 25th Feb 2021
 */
class AvdLoadingProgressBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val avd = AnimatedVectorDrawableCompat.create(context, R.drawable.ic_labs_loading_icon)!!

    init {
        setImageDrawable(avd)
        avd.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                post { avd.start() }
            }
        })
        avd.start()
    }
}