package com.pennapps.labs.pennmobile.Subletting

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SubletDetailsPagerAdapter(fragment: Fragment, private val dataModel: SublettingViewModel, private val subletNumber: Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletDetailsFragment(dataModel, subletNumber)
        } else {
            SubletCandidatesFragment(subletNumber)
        }
    }

}