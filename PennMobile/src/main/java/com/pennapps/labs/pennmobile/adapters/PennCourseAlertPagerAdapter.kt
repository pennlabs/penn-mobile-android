package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.PennCourseAlertCreateAlertFragment
import com.pennapps.labs.pennmobile.PennCourseAlertManageAlertsFragment

class PennCourseAlertPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = COUNT

    override fun createFragment(position: Int): Fragment =
        if (position == CREATE_ALERT_POSITION) {
            PennCourseAlertCreateAlertFragment()
        } else {
            PennCourseAlertManageAlertsFragment()
        }

    companion object {
        private const val CREATE_ALERT_POSITION = 0

//        private const val MANAGE_ALERTS_POSITION = 1
        const val COUNT = 2
    }
}
