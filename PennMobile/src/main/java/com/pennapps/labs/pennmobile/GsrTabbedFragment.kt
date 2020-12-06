package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.fragment_gsr_tabs.*
import kotlinx.android.synthetic.main.fragment_gsr_tabs.view.*

class GsrTabbedFragment : Fragment() {

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gsr_tabs, container, false)
        val fragmentAdapter = GsrPagerAdapter(childFragmentManager)
        viewPager = view.gsr_viewpager
        viewPager.adapter = fragmentAdapter
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.setTitle(R.string.gsr)

        tabLayout = view.gsr_tab_layout
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //displays banner if not connected
        if (!isOnline(context)) {
            internetConnectionGSR?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_gsr?.setText("Not Connected to Internet")
            internetConnectionGSR?.visibility = View.VISIBLE
        } else {
            internetConnectionGSR?.visibility = View.GONE
        }

    }
}