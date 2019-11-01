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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class GsrReservationsFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("GsrReservations")
                .putContentType("App Feature")
                .putContentId("9"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gsr_reservations, container, false)

        view.gsr_reservations_rv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

         view.gsr_reservations_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
         view.gsr_reservations_refresh_layout.setOnRefreshListener { getReservations() }

        getReservations()

        return view
    }

    private fun getReservations() {

        // get email and session id from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
        val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
        val email = sp.getString(getString(R.string.email_address), "")

        val labs = MainActivity.getLabsInstance()
        labs.getGsrReservations(email, sessionID).subscribe({ reservations ->
            mActivity.runOnUiThread {
                gsr_reservations_rv.adapter = GsrReservationsAdapter(ArrayList(reservations), false)
                loadingPanel.visibility = View.GONE
                if (reservations.size > 0) {
                    gsr_no_reservations.visibility = View.GONE
                } else {
                    gsr_no_reservations.visibility = View.VISIBLE
                }
                // stop refreshing
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        }, { throwable ->
            mActivity.runOnUiThread {
                throwable.printStackTrace()
                Toast.makeText(activity, "Error: Could not load GSR reservations", Toast.LENGTH_LONG).show()
                loadingPanel.visibility = View.GONE
                gsr_no_reservations.visibility = View.VISIBLE
                try {
                    gsr_reservations_refresh_layout.isRefreshing = false
                } catch (e: NullPointerException) {}
            }
        })
    }

}