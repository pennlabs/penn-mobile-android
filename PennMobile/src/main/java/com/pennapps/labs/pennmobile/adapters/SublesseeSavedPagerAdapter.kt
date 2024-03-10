package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.SubletteeFragment
import com.pennapps.labs.pennmobile.SubletterFragment

class SublesseeSavedPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            SubletterFragment()
        } else {
            SubletteeFragment()
        }
    }

}