package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.SubletCandidatesFragment
import com.pennapps.labs.pennmobile.SubletDetailsFragment

class SubletterListingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletDetailsFragment()
        } else {
            SubletCandidatesFragment()
        }
    }
}