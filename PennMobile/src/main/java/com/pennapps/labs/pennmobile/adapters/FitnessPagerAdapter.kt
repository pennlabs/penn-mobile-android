package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.PottruckFragment

class FitnessPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return PottruckFragment()
        }
        return PottruckFragment()
    }
}
