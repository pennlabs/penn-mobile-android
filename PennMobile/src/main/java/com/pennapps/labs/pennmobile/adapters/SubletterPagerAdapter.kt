package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.Subletting.SubletterDraftListingsFragment
import com.pennapps.labs.pennmobile.Subletting.SubletterPostedListingsFragment
import com.pennapps.labs.pennmobile.classes.SublettingViewModel

class SubletterPagerAdapter(fragment: Fragment, private val dataModel: SublettingViewModel) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletterPostedListingsFragment()
        } else {
            SubletterDraftListingsFragment(dataModel)
        }
    }

}