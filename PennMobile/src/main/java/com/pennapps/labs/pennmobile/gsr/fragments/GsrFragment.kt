package com.pennapps.labs.pennmobile.gsr.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrSearchScreen
import com.pennapps.labs.pennmobile.gsr.presentation.search.GsrSearchViewModel
import com.pennapps.labs.pennmobile.gsr.repo.endTimeParam
import com.pennapps.labs.pennmobile.gsr.repo.startTimeParam
import dagger.hilt.android.AndroidEntryPoint

/** "Book a Room" tab: hosts [GsrSearchScreen] inside the existing GSR tab pager. */
@AndroidEntryPoint
class GsrFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this)[GsrSearchViewModel::class.java] }

    // BookGsrFragment broadcasts "refresh" after a successful booking.
    private val refreshReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent,
            ) {
                viewModel.refresh()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? MainActivity)?.closeKeyboard()
        ContextCompat.registerReceiver(
            requireContext(),
            refreshReceiver,
            IntentFilter("refresh"),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onDestroy() {
        requireContext().unregisterReceiver(refreshReceiver)
        super.onDestroy()
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
                        onSlotClick = ::openBooking,
                        onLogIn = { (activity as? MainActivity)?.startLoginFragment() },
                    )
                }
            }
        }

    private fun openBooking(option: GsrBookingOption) {
        val bookGsrFragment =
            BookGsrFragment.newInstance(
                gsrID = option.roomId.toString(),
                gsrLocationCode = option.lid,
                startTime = option.startTimeParam(),
                endTime = option.endTimeParam(),
                gid = option.gid,
                roomId = option.roomId,
                roomName = option.roomName,
            )
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, bookGsrFragment)
            .addToBackStack("GSR Fragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
