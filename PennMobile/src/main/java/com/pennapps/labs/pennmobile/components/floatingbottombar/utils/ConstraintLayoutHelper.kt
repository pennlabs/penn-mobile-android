package com.pennapps.labs.pennmobile.components.floatingbottombar.utils

import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintSet

internal fun ConstraintSet.createChain(
    @IdRes firstItemId: Int,
    @IdRes secondItemId: Int,
    chainStyle: Int
) {
    val chainViews = intArrayOf(firstItemId, secondItemId)
    val chainWeights = floatArrayOf(0f, 0f)

    this.createHorizontalChain(
        firstItemId, ConstraintSet.LEFT,
        secondItemId, ConstraintSet.RIGHT,
        chainViews, chainWeights,
        chainStyle
    )
}
