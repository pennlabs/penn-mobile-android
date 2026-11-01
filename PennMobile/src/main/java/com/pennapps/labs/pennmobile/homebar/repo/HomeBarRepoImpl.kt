package com.pennapps.labs.pennmobile.homebar.repo

import android.content.SharedPreferences
import androidx.core.content.edit
import com.pennapps.labs.pennmobile.homebar.HomeBarTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class HomeBarRepoImpl
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
    ) : HomeBarRepo {
        private val _tabs = MutableStateFlow(HomeBarTab.barFor(loadPicks()))
        override val tabs: StateFlow<List<HomeBarTab>> = _tabs

        override fun setPicks(picks: Set<HomeBarTab>) {
            if (picks.size != HomeBarTab.CUSTOM_COUNT || picks.any { it.isPinned || !it.canBeInBar }) return

            sharedPreferences.edit { putStringSet(KEY_PICKS, picks.map { it.key }.toSet()) }
            _tabs.value = HomeBarTab.barFor(picks)
        }

        private fun loadPicks(): Set<HomeBarTab> {
            val keys = sharedPreferences.getStringSet(KEY_PICKS, null) ?: return HomeBarTab.DEFAULT_PICKS
            return HomeBarTab.entries.filter { it.key in keys }.toSet()
        }

        private companion object {
            const val KEY_PICKS = "home_bar_tabs"
        }
    }
