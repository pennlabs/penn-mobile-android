package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.pennapps.labs.pennmobile.adapters.SublettingPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_dining_holder.view.appbar_home_holder
import com.google.android.material.tabs.TabLayoutMediator


/**
 * A simple [Fragment] subclass.
 * Use the [SublettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 * https://tutorials.eu/viewpager2-with-fragmentstateadapter-in-android/
 */
class SublettingFragment : Fragment() {
    lateinit var sublettingPagerAdapter: SublettingPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subletting, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sublettingPagerAdapter = SublettingPagerAdapter(this)
        viewPager = view.findViewById(R.id.viewPager2)
        viewPager.adapter = sublettingPagerAdapter
        val tabLayout : TabLayout = view.findViewById(R.id.subletting_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()

    }


    private fun initAppBar(view: View) {
        (view.appbar_home_holder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
    }





}