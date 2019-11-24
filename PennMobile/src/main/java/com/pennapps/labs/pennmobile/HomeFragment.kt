package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.HomeAdapter
import com.pennapps.labs.pennmobile.classes.HomeCell
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class HomeFragment : Fragment()  {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("Home")
                .putContentType("App Feature")
                .putContentId("9"))
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
        labs.getHomePage("test_android", "5fb78cbc-692e-4167-8802-82c3eb2ddc7b").subscribe({ cells ->
            mActivity.runOnUiThread {
                val gsrBookingCell = HomeCell()
                gsrBookingCell.type = "gsr_booking"
                val buildings = ArrayList<String>()
                buildings.add("Huntsman Hall")
                buildings.add("VP Weigle")
                gsrBookingCell.buildings = buildings
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
        mActivity.setTitle(R.string.home)
        mActivity.setNav(R.id.nav_home)
    }
}