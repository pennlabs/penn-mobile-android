package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.pennapps.labs.pennmobile.adapters.DiningPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_dining.view.dining_swiperefresh
import kotlinx.android.synthetic.main.fragment_dining_holder.view.appbar_home_holder
import kotlinx.android.synthetic.main.fragment_dining_holder.view.date_view
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.appbar_home
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.date_view
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.title_view
import kotlinx.android.synthetic.main.include_main.toolbar

/**
 * A simple [Fragment] subclass.
 * Use the [SublettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 * https://tutorials.eu/viewpager2-with-fragmentstateadapter-in-android/
 */
class SublettingFragment : Fragment() {
    lateinit var pagerAdapter: SublettingAdapter
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_subletting, container, false)
        initAppBar(view)
        // Inflate the layout for this fragment
        return view
    }


    private fun initAppBar(view: View) {
        (view.appbar_home_holder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
    }





}