package com.pennapps.labs.pennmobile

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pennapps.labs.pennmobile.homebar.HomeBarTab

class MainPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle?,
) : FragmentStateAdapter(fragmentManager, lifecycle!!) {
    var tabs: List<HomeBarTab> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun createFragment(position: Int): Fragment = tabs[position].newTabFragment()

    override fun getItemCount(): Int = tabs.size

    override fun getItemId(position: Int): Long = tabs[position].ordinal.toLong()

    override fun containsItem(itemId: Long): Boolean = tabs.any { it.ordinal.toLong() == itemId }
}
