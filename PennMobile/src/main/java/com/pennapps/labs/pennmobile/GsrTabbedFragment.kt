package com.pennapps.labs.pennmobile

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentGsrTabsBinding
import com.pennapps.labs.pennmobile.utils.Utils

class GsrTabbedFragment : Fragment() {
    private lateinit var mActivity : MainActivity
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    private var _binding : FragmentGsrTabsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGsrTabsBinding.inflate(inflater, container, false)
        val view = binding.root
        initAppBar()

        mActivity = activity as MainActivity

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentAdapter = activity?.supportFragmentManager?.let { GsrPagerAdapter(it) }
        viewPager = binding.gsrViewpager
        viewPager.adapter = fragmentAdapter
        tabLayout = binding.gsrTabLayout
        tabLayout.setupWithViewPager(viewPager)
        //displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionGSR.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsr.text = "Not Connected to Internet"
            binding.internetConnectionGSR.visibility = View.VISIBLE
        } else {
            binding.internetConnectionGSR.visibility = View.GONE
        }
    }


    private fun initAppBar() {
        (binding.appbarHome.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        binding.titleView.text = getString(R.string.gsr)
        binding.dateView.text = Utils.getCurrentSystemTime()
    }
}