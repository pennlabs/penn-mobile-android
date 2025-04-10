package com.pennapps.labs.pennmobile.gsr.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class GsrPagerAdapter(
    fm: FragmentManager,
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                GsrFragment()
            } else -> {
                return GsrReservationsFragment()
            }
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Book a Room"
            else -> {
                return "My Reservations"
            }
        }
    }
}
