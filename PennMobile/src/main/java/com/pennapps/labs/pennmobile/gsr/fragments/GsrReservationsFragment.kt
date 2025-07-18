package com.pennapps.labs.pennmobile.gsr.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationsBinding
import com.pennapps.labs.pennmobile.gsr.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.isOnline
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GsrReservationsFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentGsrReservationsBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity

        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, IntentFilter("refresh"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGsrReservationsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.gsrReservationsRv.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false,
            )

        binding.gsrReservationsRefreshLayout.setColorSchemeResources(
            R.color.color_accent,
            R.color.color_primary,
        )
        binding.gsrReservationsRefreshLayout.setOnRefreshListener { getReservations() }

        getReservations()

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
        if (!isOnline(context)) {
            binding.internetConnectionGSRReservations.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsrReservations.text = "Not Connected to Internet"
            binding.internetConnectionGSRReservations.visibility = View.VISIBLE
            binding.gsrReservationsRefreshLayout.isRefreshing = false
            binding.loadingPanel.root.visibility = View.GONE
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
            binding.loadingPanel.root.visibility = View.GONE
            binding.gsrNoReservations.visibility = View.VISIBLE
        } else {
            binding.internetConnectionGSRReservations.visibility = View.GONE
        }
        // get email and session id from shared preferences

        val labs = MainActivity.studentLifeInstance

        mActivity.mNetworkManager.getAccessToken {
            val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
            val sessionID = sp.getString(getString(R.string.huntsmanGSR_SessionID), "")
            val email = sp.getString(getString(R.string.email_address), "")
            val token = sp.getString(getString(R.string.access_token), "")
            try {
                labs
                    .getGsrReservations("Bearer $token")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ reservations ->
                        binding.loadingPanel.root.visibility = View.GONE
                        try {
                            val sortedReservations = reservations?.sortedBy { it?.fromDate }
                            sortedReservations?.let {
                                binding.gsrReservationsRv.adapter =
                                    GsrReservationsAdapter(
                                        ArrayList(it.filterNotNull()),
                                    )
                                if (it.isNotEmpty()) {
                                    binding.gsrNoReservations.visibility = View.GONE
                                } else {
                                    binding.gsrNoReservations.visibility = View.VISIBLE
                                }
                            }
                            // stop refreshing
                            binding.gsrReservationsRefreshLayout.isRefreshing = false
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }, { throwable ->
                        mActivity.runOnUiThread {
                            Log.e("GsrReservationsFragment", "Error getting reservations", throwable)
                            throwable.printStackTrace()
                            binding.loadingPanel.root.visibility = View.GONE
                            try {
                                binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList())
                                binding.gsrNoReservations.visibility = View.VISIBLE
                                binding.gsrReservationsRefreshLayout.isRefreshing = false
                            } catch (e: Exception) {
                                FirebaseCrashlytics.getInstance().recordException(e)
                            }
                        }
                    })
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }
    }

    private val broadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                getReservations()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(broadcastReceiver)
    }
}
