package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.DiningPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentDiningHolderBinding
import com.pennapps.labs.pennmobile.utils.Utils

class DiningHolderFragment : Fragment() {
    lateinit var pagerAdapter: DiningPagerAdapter
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentDiningHolderBinding? = null
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
        _binding = FragmentDiningHolderBinding.inflate(inflater, container, false)
        val view = binding.root
        initAppBar()
        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = DiningPagerAdapter(childFragmentManager, lifecycle)
        binding.pager.adapter = pagerAdapter
        binding.pager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            if (position == 0) {
                tab.text = "Dining Halls"
            } else {
                tab.text = "Insights"
            }
        }.attach()
        setTitle("Dining")
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.dining)
        mActivity.setSelectedTab(MainActivity.DINING)
    }

    private fun initAppBar() {
        (binding.appbarHomeHolder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        binding.dateView.text = Utils.getCurrentSystemTime()
    }

    private fun setTitle(title: CharSequence) {
        binding.titleView.text = title
    }
}
