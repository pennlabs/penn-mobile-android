package com.pennapps.labs.pennmobile.homebar.repo

import com.pennapps.labs.pennmobile.homebar.HomeBarTab
import kotlinx.coroutines.flow.StateFlow

interface HomeBarRepo {
    val tabs: StateFlow<List<HomeBarTab>>

    /** Saves the user's 3 choices */
    fun setPicks(picks: Set<HomeBarTab>)
}
