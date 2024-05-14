package com.pennapps.labs.pennmobile.components.floatingbottombar.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.pennapps.labs.pennmobile.components.floatingbottombar.utils.clamp

class ExpandableBottomBarScrollableBehavior<V : View>(
    context: Context,
    attributeSet: AttributeSet,
) :
    ExpandableBottomBarBehavior<V>(context, attributeSet) {
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int,
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int,
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        child.translationY = clamp(child.translationY + dy, 0f, child.height + 65F)
    }
}
