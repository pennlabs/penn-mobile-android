package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.Subletting.SubletteeFragment
import com.pennapps.labs.pennmobile.Subletting.SubletterPostedListingsFragment

class SublettingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletterPostedListingsFragment()
        } else {
            SubletteeFragment()
        }
    }

}