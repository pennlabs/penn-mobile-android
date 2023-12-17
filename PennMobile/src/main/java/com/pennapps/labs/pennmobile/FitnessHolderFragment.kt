package com.pennapps.labs.pennmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.FitnessPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.databinding.FragmentFitnessHolderBinding
import com.pennapps.labs.pennmobile.utils.Utils



class FitnessHolderFragment: Fragment() {
    private lateinit var mActivity : MainActivity
    private lateinit var mView : View
    private lateinit var pagerAdapter : FitnessPagerAdapter

    private var _binding : FragmentFitnessHolderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentFitnessHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        // initialize app bar and swipe refresh
        initAppBar()

        pagerAdapter = FitnessPagerAdapter(this)
        binding.pager.adapter = pagerAdapter
        binding.pager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            if (position == 0) {
                tab.text = "Pottruck"
            } else {
                tab.text = "Favorites"
            }
        }.attach()
    }


    /**
     * Initialize the app bar of the fragment and
     * fills in the textViews for the title/date
     */
    private fun initAppBar() {
        val appBarLayout : AppBarLayout = binding.appbarHomeHolder
        val titleView : TextView = binding.titleView
        val dateView : TextView = binding.dateView

        (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        titleView.text = getString(R.string.fitness)
        dateView.text = Utils.getCurrentSystemTime()
    }
}