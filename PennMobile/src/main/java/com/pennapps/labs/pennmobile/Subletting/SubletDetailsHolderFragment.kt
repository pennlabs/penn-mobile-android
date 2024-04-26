package com.pennapps.labs.pennmobile.Subletting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.adapters.SubletDetailsPagerAdapter
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.SublettingViewModel
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_dining_holder.view.appbar_home_holder

//Pulls existing datamodel from previous
class SubletDetailsHolderFragment(private val dataModel: SublettingViewModel, private val subletNumber: Int) : Fragment() {
    lateinit var subletDetailsPagerAdapter: SubletDetailsPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2
    private lateinit var mStudentLife : StudentLife




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        mStudentLife = MainActivity.studentLifeInstance

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subletter_listing, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subletDetailsPagerAdapter = SubletDetailsPagerAdapter(this, dataModel, subletNumber)
        viewPager = view.findViewById(R.id.listing_view_pager)
        viewPager.adapter = subletDetailsPagerAdapter
        val tabLayout : TabLayout = view.findViewById(R.id.listing_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0) {
                tab.text = "Details"
            } else {
                tab.text = "Candidates"
            }
        }.attach()




    }


    private fun initAppBar(view: View) {
        (view.appbar_home_holder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
    }
}