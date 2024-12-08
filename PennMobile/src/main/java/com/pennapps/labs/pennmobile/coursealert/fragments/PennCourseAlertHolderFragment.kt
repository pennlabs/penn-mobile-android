package com.pennapps.labs.pennmobile.coursealert.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.coursealert.adapters.PennCourseAlertPagerAdapter
import com.pennapps.labs.pennmobile.databinding.FragmentPennCourseAlertHolderBinding
import com.pennapps.labs.pennmobile.utils.Utils

class PennCourseAlertHolderFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var pagerAdapter: PennCourseAlertPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentPennCourseAlertHolderBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPennCourseAlertHolderBinding.inflate(inflater, container, false)
        val view = binding.root
        initAppBar(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        mActivity.setSelectedTab(MainActivity.PCA)
        super.onResume()
    }

    private fun initAppBar(view: View?) {
        binding.titleView.text = "Penn Course Alert"
        binding.dateView.text = Utils.getCurrentSystemTime()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = PennCourseAlertPagerAdapter(this)
        viewPager = view.findViewById(R.id.PCAPager)
        viewPager.adapter = pagerAdapter

        tabLayout = view.findViewById(R.id.pca_tab_layout)
        TabLayoutMediator(tabLayout, binding.PCAPager) { tab, position ->
            if (position == 0) {
                tab.text = "Create Alert"
            } else {
                tab.text = "Manage Alerts"
            }
        }.attach()
    }
}
