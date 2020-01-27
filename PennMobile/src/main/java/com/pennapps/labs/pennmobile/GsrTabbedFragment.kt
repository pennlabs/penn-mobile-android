package com.pennapps.labs.pennmobile

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.*

class GsrTabbedFragment : Fragment() {

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gsr_tabs, container, false)
        val fragmentAdapter = GsrPagerAdapter(childFragmentManager)
        viewPager = view.gsr_viewpager
        viewPager.adapter = fragmentAdapter
        activity?.setTitle(R.string.gsr)

        tabLayout = view.gsr_tab_layout
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}