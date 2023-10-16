package com.pennapps.labs.pennmobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennapps.labs.pennmobile.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import kotlinx.android.synthetic.main.fragment_gsr_reservations.*
import kotlinx.android.synthetic.main.fragment_gsr_reservations.view.*
import kotlinx.android.synthetic.main.loading_panel.*

class GsrReservationsFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, IntentFilter("refresh"))

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "10")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "GsrReservations")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "App Feature")
        FirebaseAnalytics.getInstance(mActivity).logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isOnline(context)) {
            internetConnectionGSRReservations?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_gsr_reservations?.text = "Not Connected to Internet"
            internetConnectionGSRReservations?.visibility = View.VISIBLE
            gsr_reservations_refresh_layout?.isRefreshing = false
            loadingPanel?.visibility = View.GONE
            gsr_no_reservations?.visibility = View.VISIBLE
        } else {
            internetConnectionGSRReservations?.visibility = View.GONE
        }
    }

    private fun getReservations() {
        if (!isOnline(context)) {
            internetConnectionGSRReservations?.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            internetConnection_message_gsr_reservations?.text = "Not Connected to Internet"
            internetConnectionGSRReservations?.visibility = View.VISIBLE
            gsr_reservations_refresh_layout?.isRefreshing = false
            gsr_reservations_rv?.adapter = GsrReservationsAdapter(ArrayList())
            loadingPanel?.visibility = View.GONE
            gsr_no_reservations?.visibility = View.VISIBLE
        } else {
            internetConnectionGSRReservations?.visibility = View.GONE
        }
        // get email and session id from shared preferences

        val labs = MainActivity.studentLifeInstance

        OAuth2NetworkManager(mActivity).getAccessToken {
            val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
            val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
            val email = sp.getString(getString(R.string.email_address), "")
            val token = sp.getString(getString(R.string.access_token), "")
            labs.getGsrReservations("Bearer $token").subscribe({ reservations ->
                mActivity.runOnUiThread {
                    gsr_reservations_rv?.adapter = GsrReservationsAdapter(ArrayList(reservations))
                    loadingPanel?.visibility = View.GONE
                    if (reservations.size > 0) {
                        gsr_no_reservations?.visibility = View.GONE
                    } else {
                        gsr_no_reservations?.visibility = View.VISIBLE
                    }
                    // stop refreshing
                    gsr_reservations_refresh_layout?.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("GsrReservationsFragment", "Error getting reservations", throwable)
                    gsr_reservations_rv?.adapter = GsrReservationsAdapter(ArrayList())
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    gsr_no_reservations?.visibility = View.VISIBLE
                    gsr_reservations_refresh_layout?.isRefreshing = false
                }
            })
        }
    }

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getReservations()
        }
    }

}