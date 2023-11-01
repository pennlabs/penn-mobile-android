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
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationsBinding

import kotlinx.android.synthetic.main.loading_panel.loadingPanel

class GsrReservationsFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    private var _binding : FragmentGsrReservationsBinding? = null
    private val binding get() = _binding!!


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
        _binding = FragmentGsrReservationsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.gsrReservationsRv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)

        binding.gsrReservationsRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        binding.gsrReservationsRefreshLayout.setOnRefreshListener { getReservations() }

        getReservations()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isOnline(context)) {
            binding.internetConnectionGSRReservations.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsrReservations.text = "Not Connected to Internet"
            binding.internetConnectionGSRReservations.visibility = View.VISIBLE
            binding.gsrReservationsRefreshLayout.isRefreshing = false
            loadingPanel?.visibility = View.GONE
            binding.gsrNoReservations.visibility = View.VISIBLE
        } else {
            binding.internetConnectionGSRReservations.visibility = View.GONE
        }
    }

    private fun getReservations() {
        if (!isOnline(context)) {
            binding.internetConnectionGSRReservations.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsrReservations.text = "Not Connected to Internet"
            binding.internetConnectionGSRReservations.visibility = View.VISIBLE
            binding.gsrReservationsRefreshLayout.isRefreshing = false
            binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList())
            loadingPanel?.visibility = View.GONE
            binding.gsrNoReservations.visibility = View.VISIBLE
        } else {
            binding.internetConnectionGSRReservations.visibility = View.GONE
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
                    binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList(reservations))
                    loadingPanel?.visibility = View.GONE

                    if (reservations.size > 0) {
                        binding.gsrNoReservations.visibility = View.GONE
                    } else {
                        binding.gsrNoReservations.visibility = View.VISIBLE
                    }
                    // stop refreshing
                    binding.gsrReservationsRefreshLayout.isRefreshing = false
                }
            }, { throwable ->
                mActivity.runOnUiThread {
                    Log.e("GsrReservationsFragment", "Error getting reservations", throwable)
                    binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList())
                    throwable.printStackTrace()
                    loadingPanel?.visibility = View.GONE
                    binding.gsrNoReservations.visibility = View.VISIBLE
                    binding.gsrReservationsRefreshLayout.isRefreshing = false
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