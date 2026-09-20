package com.pennapps.labs.pennmobile.gsr.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.MainActivity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentGsrReservationsBinding
import com.pennapps.labs.pennmobile.gsr.adapters.GsrReservationsAdapter
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.viewmodels.GsrReservationsViewModel
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import com.pennapps.labs.pennmobile.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        ContextCompat.registerReceiver(
            mActivity,
            broadcastReceiver,
            IntentFilter("refresh"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
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
                viewModel.cancelSuccess
                    .onEach { success ->
                        if (success) {
                            onCancelSucceeded()
                            viewModel.resetCancelSuccess()
                        }
                    }
                    .launchIn(this)

                viewModel.error
                    .onEach { error ->
                        if (error != null) {
                            pendingCancelBookingId = null
                            Toast.makeText(
                                requireContext(),
                                error.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .launchIn(this)

                combine(
                    viewModel.reservations,
                    viewModel.isLoadingReservations
                ) { reservations, isLoading ->
                    reservations to isLoading
                }
                    .onEach { (reservations, isLoading) ->
                        renderReservations(reservations, isLoading)
                    }
                    .launchIn(this)
            }
        }
    }

    private fun renderReservations(reservations: List<GSRReservation>, isLoading: Boolean) {
        _binding?.let { binding ->
            if (isLoading) {
                if (binding.gsrReservationsRv.adapter?.itemCount == 0 || binding.gsrReservationsRv.adapter == null) {
                    binding.loadingPanel.root.visibility = View.VISIBLE
                    binding.gsrNoReservations.visibility = View.GONE
                }
            } else {
                binding.loadingPanel.root.visibility = View.GONE
                binding.gsrReservationsRefreshLayout.isRefreshing = false
                
                binding.gsrReservationsRv.adapter =
                    GsrReservationsAdapter(
                        ArrayList(reservations),
                        ::onCancelRequested,
                    )

                if (reservations.isNotEmpty()) {
                    binding.gsrNoReservations.visibility = View.GONE
                } else {
                    binding.gsrNoReservations.visibility = View.VISIBLE
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
            viewModel.fetchReservations()
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
        mActivity.unregisterReceiver(broadcastReceiver)
    }
}
