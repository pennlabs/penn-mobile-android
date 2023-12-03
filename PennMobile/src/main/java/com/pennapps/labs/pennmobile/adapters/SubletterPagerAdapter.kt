package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.SubletterDraftListingsFragment
import com.pennapps.labs.pennmobile.SubletterPostedListingsFragment

class SubletterPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletterPostedListingsFragment()
        } else {
            SubletterDraftListingsFragment()
        }
    }

}