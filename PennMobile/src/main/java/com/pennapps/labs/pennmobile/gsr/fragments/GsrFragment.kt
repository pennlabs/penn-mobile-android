package com.pennapps.labs.pennmobile.gsr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager.widget.ViewPager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrBooked
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrSearchScreen
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val MY_RESERVATIONS_TAB = 1

/** "Book a Room" tab: hosts [GsrSearchScreen] inside the existing GSR tab pager. */
@AndroidEntryPoint
class GsrFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this)[GsrSearchViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.closeKeyboard()
    }

    /** Resumed each time the GSR tab is opened (its pager caps us at STARTED otherwise). */
    override fun onResume() {
        super.onResume()
        viewModel.resetToNow()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                AppTheme {
                    GsrSearchScreen(
                        state = state,
                        onSelectLocation = viewModel::selectLocation,
                        onSelectDate = viewModel::selectDate,
                        onSelectTime = viewModel::selectTime,
                        onSelectDuration = viewModel::selectDuration,
                        onSortByTimeChange = viewModel::setSortByTime,
                        onRefresh = viewModel::refresh,
                        onSlotClick = viewModel::openBooking,
                        onConfirmBooking = viewModel::confirmBooking,
                        onDismissBooking = viewModel::dismissBooking,
                        onLogIn = { (activity as? MainActivity)?.startLoginFragment() },
                    )
                }
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookingEvents.collect(::onBooked)
            }
        }
    }

    private fun onBooked(booked: GsrBooked) {
        Toast.makeText(requireContext(), getString(R.string.gsr_booked, booked.roomName), Toast.LENGTH_LONG).show()
        (view?.parent as? ViewPager)?.currentItem = MY_RESERVATIONS_TAB
    }
}
