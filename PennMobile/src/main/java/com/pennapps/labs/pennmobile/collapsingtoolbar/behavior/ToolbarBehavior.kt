package com.pennapps.labs.pennmobile.collapsingtoolbar.behavior

import android.graphics.*
import android.os.Build
import android.renderscript.RenderScript
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.floatingbottombar.utils.clamp
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlin.math.abs


/**
 * This behavior animates the toolbar elements (toolbarTitle, toolBarDate, e.t.c) as
 * the recyclerView in a fragment scrolls.
 *
 * - Davies Lumumba 2020
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class ToolbarBehavior : CoordinatorLayout.Behavior<AppBarLayout>() {
    private lateinit var toolbar: View
    private lateinit var divider: View
    private lateinit var date: View
    private lateinit var profile: View
    private lateinit var title: View
    private lateinit var bottomBar: View
    private lateinit var container: View

    private var toolbarOriginalHeight: Float = -1f
    private var toolbarCollapsedHeight: Float = -1f
    private var viewsSet = false
    private var minScale = 0.65f

    /**
     * Set the required view variables. Only accessed once because of the viewsSet variable.
     */
    private fun getViews(child: AppBarLayout) {
        if (viewsSet) return
        viewsSet = true

        toolbar = child.findViewById(R.id.appbar_container)
        divider = child.findViewById(R.id.divider)
        date = child.findViewById(R.id.date_view)
        profile = child.findViewById(R.id.profile)
        title = child.findViewById(R.id.title_view)
        bottomBar = child.rootView.findViewById(R.id.expandable_bottom_bar)

        toolbarOriginalHeight = toolbar.layoutParams.height.toFloat()
        toolbarCollapsedHeight = toolbarOriginalHeight * minScale
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout,
                                   child: AppBarLayout, target: View,
                                   dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        bottomBar.translationY = clamp(bottomBar.translationY + dy,
                0f, bottomBar.height + 65F)
    }

    /**
     * Consume if vertical scroll because we don't care about other scrolls
     */
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View,
                                     target: View, axes: Int, type: Int): Boolean {
        getViews(child)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }


    /**
     * Perform actual animation by determining the dY amount
     */
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
                                dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
                                type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        getViews(child)

        if (dyConsumed > 0) {

            // scroll up:
            if (toolbar.layoutParams.height > toolbarCollapsedHeight) {

                divider.visibility = View.VISIBLE
                //--- shrink toolbar
                val height = toolbar.layoutParams.height - dyConsumed
                toolbar.layoutParams.height = if (height < toolbarCollapsedHeight) toolbarCollapsedHeight.toInt() else height
                toolbar.requestLayout()

                //--- translate up drawer icon
                var translate: Float = (toolbarOriginalHeight - toolbar.layoutParams.height) / (toolbarOriginalHeight - toolbarCollapsedHeight)
                translate *= toolbarOriginalHeight
                date.translationY = -translate
                profile.translationY = -translate

                //--- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                title.scaleX = if (scale < minScale) minScale else scale
                title.scaleY = title.scaleX

                // Gets the x distance the title should move to get to the centre
                val translateX = (toolbar.measuredWidth / 2) -
                        ((title.layoutParams as FrameLayout.LayoutParams).marginStart
                                + title.measuredWidth / 2).toFloat()

                // Maps the x translation to the shifting toolbar height
                title.translationX = (toolbarOriginalHeight - toolbar.layoutParams.height) /
                        (toolbarOriginalHeight - toolbarCollapsedHeight) * translateX


                // Maps the y translation to the shifting toolbar height
                title.translationY = (toolbarOriginalHeight - toolbar.layoutParams.height) /
                        (toolbarOriginalHeight - toolbarCollapsedHeight)
            }
        } else if (dyUnconsumed < 0) {

            // scroll down
            if (toolbar.layoutParams.height < toolbarOriginalHeight) {
                divider.visibility = View.INVISIBLE
                //--- expand toolbar
                // subtract because dyUnconsumed is < 0
                val height = toolbar.layoutParams.height - dyUnconsumed
                toolbar.layoutParams.height = if (height > toolbarOriginalHeight) toolbarOriginalHeight.toInt() else height
                toolbar.requestLayout()

                //--- translate down  drawer icon
                var translate: Float = (toolbarOriginalHeight - toolbar.layoutParams.height) / (toolbarOriginalHeight - toolbarCollapsedHeight)
                translate *= toolbarOriginalHeight
                date.translationY = -translate
                profile.translationY = -translate

                //--- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                title.scaleX = if (scale < minScale) minScale else scale
                title.scaleY = title.scaleX

                // Gets the x distance the title should move to get to the centre
                val translateX = (toolbar.measuredWidth / 2) -
                        ((title.layoutParams as FrameLayout.LayoutParams).marginStart
                                + title.measuredWidth / 2).toFloat()

                // Maps the x translation to the shifting toolbar height
                title.translationX = (toolbarOriginalHeight - toolbar.layoutParams.height) /
                        (toolbarOriginalHeight - toolbarCollapsedHeight) * translateX

                // Maps the y translation to the shifting toolbar height
                title.translationY = (toolbarOriginalHeight - toolbar.layoutParams.height) /
                        (toolbarOriginalHeight - toolbarCollapsedHeight)
            }
        }
    }
}