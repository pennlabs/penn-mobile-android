package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.DiningFragment
import com.pennapps.labs.pennmobile.DiningInsightsFragment
import java.util.*

class DiningPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle?) : FragmentStateAdapter(fragmentManager, lifecycle!!) {
    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return DiningFragment()
        } else {
            return DiningInsightsFragment()
        }
    }

    override fun getItemCount(): Int {
        return COUNT
    }

    override fun getItemId(position: Int): Long {
        if (position == HOME_POSITION) {
            return HOME_POSITION.toLong()
        } else if (position == INSIGHTS_POSITION) {
            return INSIGHTS_POSITION.toLong()
        }
        return createFragment(position).hashCode().toLong()
    }

    companion object {
        private const val HOME_POSITION = 0
        private const val INSIGHTS_POSITION = 1
        val COUNT = 2
    }
}