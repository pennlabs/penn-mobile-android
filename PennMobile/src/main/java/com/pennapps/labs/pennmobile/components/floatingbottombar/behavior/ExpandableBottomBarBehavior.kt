package com.pennapps.labs.pennmobile.components.floatingbottombar.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

open class ExpandableBottomBarBehavior<V: View>: CoordinatorLayout.Behavior<V> {

    constructor(): super()

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            updateSnackBar(child, dependency)
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    private fun updateSnackBar(child: View, snackBarLayout: Snackbar.SnackbarLayout) {
        if (snackBarLayout.layoutParams is CoordinatorLayout.LayoutParams) {
            val params = snackBarLayout.layoutParams as CoordinatorLayout.LayoutParams

            params.anchorId = child.id
            params.anchorGravity = Gravity.TOP
            params.gravity = Gravity.TOP
            snackBarLayout.layoutParams = params
        }
    }
}
