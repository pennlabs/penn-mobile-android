package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.Subletting.SublesseeAppliedListingsHolder
import com.pennapps.labs.pennmobile.Subletting.SublesseeSavedListingsHolderFragment

class SublesseeSavedPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            //actual saved listings
            SublesseeSavedListingsHolderFragment()
        } else {
            //applied, offers
            SublesseeAppliedListingsHolder()
        }
    }

}