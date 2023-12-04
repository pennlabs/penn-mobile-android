package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.SubletterPagerAdapter

// TODO: Rename parameter arguments, choose names that match


/**
 * A simple [Fragment] subclass.
 * Use the [SubletterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubletterFragment : Fragment() {
    lateinit var subletterPagerAdapter: SubletterPagerAdapter
    private lateinit var mActivity: MainActivity
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_subletter_view, container, false)
        viewPager = rootView.findViewById(R.id.subletter_view_pager)
        subletterPagerAdapter = SubletterPagerAdapter(this)
        viewPager.adapter = subletterPagerAdapter

        val tabLayout: TabLayout = rootView.findViewById(R.id.subletter_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0) {
                tab.text = "Posted"
            } else {
                tab.text = "Drafts"
            }

        }.attach()

        return rootView

    }





}