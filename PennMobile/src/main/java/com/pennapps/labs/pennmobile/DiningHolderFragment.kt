package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pennapps.labs.pennmobile.adapters.DiningPagerAdapter
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_dining.view.*
import kotlinx.android.synthetic.main.fragment_dining_holder.*
import kotlinx.android.synthetic.main.fragment_dining_holder.view.*

class DiningHolderFragment : Fragment() {

    lateinit var pagerAdapter: DiningPagerAdapter
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dining_holder, container, false)
        view.dining_swiperefresh?.setOnRefreshListener { getConnected() }
        view.dining_swiperefresh?.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        getConnected()
        initAppBar(view)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = DiningPagerAdapter(childFragmentManager, lifecycle)
        pager?.setAdapter(pagerAdapter)
        pager.setUserInputEnabled(false)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            if (position == 0) {
                tab.text = "Hours"
            } else {
                tab.text = "Insights"
            }
        }.attach()
        setTitle("Dining")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getConnected() {
        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionDiningHolder?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_dining_holder?.text = getString(R.string.internet_error)
            internetConnectionDiningHolder?.visibility = View.VISIBLE
            // loadingPanel?.visibility = View.GONE
        } else {
            internetConnectionDiningHolder?.visibility = View.GONE
            // loadingPanel?.visibility = View.GONE
            // dining_swiperRefresh_holder?.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        //mActivity.toolbar.visibility = View.GONE
        mActivity.setTitle(R.string.dining)
        if (Build.VERSION.SDK_INT > 17) {
            mActivity.setSelectedTab(MainActivity.DINING)
        }
    }

    private fun initAppBar(view: View) {
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home_holder.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.date_view.text = Utils.getCurrentSystemTime()
    }

    private fun setTitle(title: CharSequence) {
        title_view.text = title
    }
}