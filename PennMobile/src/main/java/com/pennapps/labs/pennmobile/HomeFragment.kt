package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.classes.HomeCell
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class HomeFragment : Fragment()  {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "9")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.home_cells_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        view.home_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        view.home_refresh_layout.setOnRefreshListener { getHomePage() }

        getHomePage()

        return view
    }

    private fun getHomePage() {

        // get session id from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val sessionid = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")

        // get API data
        val labs = MainActivity.getLabsInstance() //TODO: get for an account id
        labs.getHomePage("test_android", null, sessionid).subscribe({ cells ->
            mActivity.runOnUiThread {
                val gsrBookingCell = HomeCell()
                gsrBookingCell.type = "gsr_booking"
                gsrBookingCell.buildings = arrayListOf("Huntsman Hall", "VP Weigle")
                cells?.add(cells?.size - 1, gsrBookingCell)
                home_cells_rv?.adapter = HomeAdapter(ArrayList(cells))
                loadingPanel?.visibility = View.GONE
                home_refresh_layout?.isRefreshing = false
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load Home page", Toast.LENGTH_LONG).show()
                loadingPanel?.visibility = View.GONE
                home_refresh_layout?.isRefreshing = false
            }
        })
    }

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getHomePage()
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.home)
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(0)
        }
    }
}