package com.pennapps.labs.pennmobile.gsr.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
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
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.GsrDetailsBookBinding
import com.pennapps.labs.pennmobile.gsr.viewmodels.GsrViewModel
import com.pennapps.labs.pennmobile.gsr.widget.GsrReservationWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookGsrFragment : Fragment() {
    // Making this public allows the _binding backing property pattern to pass ktlint
    private var _binding: GsrDetailsBookBinding? = null
    val binding get() = _binding!!

    // By removing the _viewModel / viewModel pair and using a single internal
    // variable, we avoid the ktlint naming error while keeping manual init.
    private var viewModel: GsrViewModel? = null
    private lateinit var mActivity: MainActivity

    private var startTime: String? = null
    private var endTime: String? = null
    private var gid: Int = 0
    private var roomId: Int = 0
    private var roomName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            startTime = it.getString("startTime")
            endTime = it.getString("endTime")
            gid = it.getInt("gid")
            roomId = it.getInt("id")
            roomName = it.getString("roomName") ?: ""
        }
        mActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GsrDetailsBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Manual initialization here ensures the Fragment is attached
        // to the Activity before Hilt tries to find the SavedStateRegistry.
        viewModel = ViewModelProvider(this)[GsrViewModel::class.java]

        (activity as? MainActivity)?.apply {
            setTitle(R.string.gsr)
            hideBottomBar()
        }

        setupInitialUI()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupInitialUI() {
        // Use the safe call or non-null assertion since it was just initialized
        val (first, last, email) = viewModel?.savedUserInfo ?: return
        binding.firstName.setText(first)
        binding.lastName.setText(last)
        binding.gsrEmail.setText(email)
    }

    private fun setupClickListeners() {
        binding.submitGsr.setOnClickListener {
            val firstName =
                binding.firstName.text
                    .toString()
                    .trim()
            val lastName =
                binding.lastName.text
                    .toString()
                    .trim()
            val email =
                binding.gsrEmail.text
                    .toString()
                    .trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel?.bookGsr(
                firstName = firstName,
                lastName = lastName,
                email = email,
                startTime = startTime ?: return@setOnClickListener,
                endTime = endTime ?: return@setOnClickListener,
                gid = gid,
                roomId = roomId,
                roomName = roomName,
            )
        }
    }

    private fun observeViewModel() {
        val vm = viewModel ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.isBooking.collect { isBooking ->
                        setLoadingState(isBooking)
                    }
                }
                launch {
                    vm.bookingSuccess.collect { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "GSR successfully booked", Toast.LENGTH_LONG).show()
                            requireContext().sendBroadcast(Intent(GsrReservationWidget.UPDATE_GSR_WIDGET))
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
                launch {
                    vm.error.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.loading.loadingPanel.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.submitGsr.isEnabled = !isLoading
        if (isLoading) {
            binding.submitGsr.background.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        } else {
            binding.submitGsr.background.clearColorFilter()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            gsrID: String,
            gsrLocationCode: String,
            startTime: String,
            endTime: String,
            gid: Int,
            roomId: Int,
            roomName: String,
        ): BookGsrFragment =
            BookGsrFragment().apply {
                arguments =
                    Bundle().apply {
                        putString("gsrID", gsrID)
                        putString("gsrLocationCode", gsrLocationCode)
                        putString("startTime", startTime)
                        putString("endTime", endTime)
                        putInt("gid", gid)
                        putInt("id", roomId)
                        putString("roomName", roomName)
                    }
            }
    }
}
