package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.PennCourseAlertPagerAdapter
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.*
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.view.date_view
import kotlinx.android.synthetic.main.fragment_penn_course_alert_holder.view.title_view

class PennCourseAlertHolderFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var pagerAdapter: PennCourseAlertPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(
            R.layout.fragment_penn_course_alert_holder, container,
            false
        )
        initAppBar(view)
        return view
    }

    override fun onResume() {
        mActivity.setSelectedTab(MainActivity.PCA)
        super.onResume()
    }

    private fun initAppBar(view: View?) {
        view?.title_view?.text = "Penn Course Alert"
        view?.date_view?.text = Utils.getCurrentSystemTime()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = PennCourseAlertPagerAdapter(this)
        viewPager = view.findViewById(R.id.PCAPager)
        viewPager.adapter = pagerAdapter

        tabLayout = view.findViewById(R.id.pca_tab_layout)
        TabLayoutMediator(tabLayout, PCAPager) { tab, position ->
            if (position == 0) {
                tab.text = "Create Alert"
            } else {
                tab.text = "Manage Alerts"
            }
        }.attach()
    }
}