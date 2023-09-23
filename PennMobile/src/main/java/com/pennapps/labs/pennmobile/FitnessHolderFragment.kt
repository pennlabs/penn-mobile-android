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
import com.pennapps.labs.pennmobile.utils.Utils

import kotlinx.android.synthetic.main.fragment_fitness_holder.pager
import kotlinx.android.synthetic.main.fragment_fitness_holder.tabLayout

class FitnessHolderFragment: Fragment() {
    private lateinit var mActivity : MainActivity
    private lateinit var mView : View
    private lateinit var pagerAdapter : FitnessPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_fitness_holder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        // initialize app bar and swipe refresh
        initAppBar()

        pagerAdapter = FitnessPagerAdapter(this)
        pager?.adapter = pagerAdapter
        pager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, pager) { tab, position ->
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
        val appBarLayout : AppBarLayout = mView.findViewById(R.id.appbar_home_holder)
        val titleView : TextView = mView.findViewById(R.id.title_view)
        val dateView : TextView = mView.findViewById(R.id.date_view)

        (appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        titleView.text = getString(R.string.fitness)
        dateView.text = Utils.getCurrentSystemTime()
    }
}