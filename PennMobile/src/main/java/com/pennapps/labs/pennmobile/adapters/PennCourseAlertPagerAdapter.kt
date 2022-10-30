package com.pennapps.labs.pennmobile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.PennCourseAlertCreateAlert
import com.pennapps.labs.pennmobile.PennCourseAlertManageAlerts

class PennCourseAlertPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return COUNT;
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == CREATE_ALERT_POSITION) {
            PennCourseAlertCreateAlert()
        } else {
            PennCourseAlertManageAlerts()
        }
    }

    companion object {
        private const val CREATE_ALERT_POSITION = 0
//        private const val MANAGE_ALERTS_POSITION = 1
        const val COUNT = 2
    }
}