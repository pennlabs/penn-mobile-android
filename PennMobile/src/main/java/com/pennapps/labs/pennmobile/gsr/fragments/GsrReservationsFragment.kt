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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationsBinding
import com.pennapps.labs.pennmobile.gsr.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.viewmodels.GsrReservationsViewModel
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import com.pennapps.labs.pennmobile.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@AndroidEntryPoint
class GsrReservationsFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var viewModel: GsrReservationsViewModel

    private var _binding: FragmentGsrReservationsBinding? = null
    val binding get() = _binding!!

    private var pendingCancelBookingId: String? = null

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

        viewModel = ViewModelProvider(this)[GsrReservationsViewModel::class.java]

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
        observeViewModel()

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

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cancelSuccess.collect { success ->
                        if (success) {
                            onCancelSucceeded()
                            viewModel.resetCancelSuccess()
                        }
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            pendingCancelBookingId = null
                            Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun onCancelRequested(reservation: GSRReservation) {
        pendingCancelBookingId = reservation.bookingId
        viewModel.cancelGsr(
            reservation.bookingId,
            isHuntsmanReservation = reservation.info == null,
        )
    }

    private fun onCancelSucceeded() {
        val bookingId = pendingCancelBookingId ?: return
        pendingCancelBookingId = null

        mActivity.sendBroadcast(Intent(GsrReservationWidget.UPDATE_GSR_WIDGET))

        val adapter = binding.gsrReservationsRv.adapter as? GsrReservationsAdapter
        val index = adapter?.indexOfBookingId(bookingId) ?: return

        adapter.removeAt(index)

        if (adapter.itemCount == 0) {
            binding.gsrNoReservations.visibility = View.VISIBLE
        }
    }

    private fun getReservations() {
        // Early return if binding is null
        _binding ?: return

        if (!isOnline(context)) {
            binding.internetConnectionGSRReservations.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsrReservations.text = "Not Connected to Internet"
            binding.internetConnectionGSRReservations.visibility = View.VISIBLE
            binding.gsrReservationsRefreshLayout.isRefreshing = false
            binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList(), ::onCancelRequested)
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
                        // Check if binding is still valid
                        _binding?.let { binding ->
                            binding.loadingPanel.root.visibility = View.GONE
                            try {
                                val sortedReservations = reservations?.sortedBy { it?.fromDate }
                                sortedReservations?.let {
                                    binding.gsrReservationsRv.adapter =
                                        GsrReservationsAdapter(
                                            ArrayList(it.filterNotNull()),
                                            ::onCancelRequested,
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
                        }
                    }, { throwable ->
                        mActivity.runOnUiThread {
                            // Check if binding is still valid
                            _binding?.let { binding ->
                                Log.e("GsrReservationsFragment", "Error getting reservations", throwable)
                                throwable.printStackTrace()
                                binding.loadingPanel.root.visibility = View.GONE
                                try {
                                    binding.gsrReservationsRv.adapter = GsrReservationsAdapter(ArrayList(), ::onCancelRequested)
                                    binding.gsrNoReservations.visibility = View.VISIBLE
                                    binding.gsrReservationsRefreshLayout.isRefreshing = false
                                } catch (e: Exception) {
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                }
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
