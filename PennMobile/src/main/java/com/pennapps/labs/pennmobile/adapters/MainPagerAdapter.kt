package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.DiningHolderFragment
import com.pennapps.labs.pennmobile.GsrTabbedFragment
import com.pennapps.labs.pennmobile.HomeFragment
import com.pennapps.labs.pennmobile.LaundryFragment
import com.pennapps.labs.pennmobile.Subletting.SublettingFragment
import com.pennapps.labs.pennmobile.morefragments.MoreFragment

class MainPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle?,
) : FragmentStateAdapter(fragmentManager, lifecycle!!) {
    override fun createFragment(position: Int): Fragment =
        when (position) {
            HOME_POSITION -> HomeFragment()
            DINING_POSITION -> DiningHolderFragment()
            GSR_POSITION -> GsrTabbedFragment()
            LAUNDRY_POSITION -> SublettingFragment()
            MORE_POSITION -> MoreFragment()
            else -> HomeFragment()
        }

    override fun getItemCount(): Int = COUNT

    override fun getItemId(position: Int): Long = if (position < COUNT) position.toLong() else createFragment(position).hashCode().toLong()

    companion object {
        const val HOME_POSITION = 0
        const val DINING_POSITION = 1
        const val GSR_POSITION = 2
        const val LAUNDRY_POSITION = 3
        const val MORE_POSITION = 4
        const val COUNT = 5
    }
}
