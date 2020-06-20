package com.pennapps.labs.pennmobile.collapsingtoolbar.behavior

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs


abstract class AppBarStateChangedListener : OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    private var mCurrentState = State.IDLE
    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when {
            verticalOffset == 0 -> {
                setCurrentStateAndNotify(appBarLayout, State.EXPANDED)
            }
            abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                setCurrentStateAndNotify(appBarLayout, State.COLLAPSED)
            }
            else -> {
                setCurrentStateAndNotify(appBarLayout, State.IDLE)
            }
        }
    }

    private fun setCurrentStateAndNotify(appBarLayout: AppBarLayout, state: State) {
        if (mCurrentState != state) {
            onStateChanged(appBarLayout, state)
        }
        mCurrentState = state
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
}