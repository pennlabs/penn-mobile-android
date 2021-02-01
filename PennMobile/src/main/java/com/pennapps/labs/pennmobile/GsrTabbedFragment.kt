package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_dining.*
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.*
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.appbar_home
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.date_view
import kotlinx.android.synthetic.main.fragment_dining.view.title_view as title_view1

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

        initAppBar(view)
        tabLayout = view.gsr_tab_layout
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    private fun initAppBar(view: View) {
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.title_view.text = getString(R.string.gsr)
        view.date_view.text = Utils.getCurrentSystemTime()
    }
}