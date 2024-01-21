package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.SubletterListingPagerAdapter
import com.pennapps.labs.pennmobile.adapters.SubletterPagerAdapter
import com.pennapps.labs.pennmobile.adapters.SublettingPagerAdapter
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import kotlinx.android.synthetic.main.fragment_dining_holder.view.appbar_home_holder

class SubletterListingHolderFragment : Fragment() {
    lateinit var subletterListingPagerAdapter: SubletterListingPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subletter_listing, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subletterListingPagerAdapter = SubletterListingPagerAdapter(this)
        viewPager = view.findViewById(R.id.viewPager2)
        viewPager.adapter = subletterListingPagerAdapter


    }



}