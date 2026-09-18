package com.pennapps.labs.pennmobile.laundry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.compose.utils.SnackBarEvent
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.StartOutcome
import com.pennapps.labs.pennmobile.laundry.domain.usecase.StartLaundryMonitorUseCase
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaundryMonitorViewModel
    @Inject
    constructor(
        repo: LaundryMonitorRepo,
        private val startMonitoring: StartLaundryMonitorUseCase,
    ) : ViewModel() {
        /**
         * Backed by the repo rather than local state, so the button also reflects the monitor
         * stopping itself after it notifies or times out.
         */
        val monitorMode: StateFlow<MonitorMode> = repo.monitorMode

        private val _snackBarEvent = MutableStateFlow<SnackBarEvent>(SnackBarEvent.None)
        val snackBarEvent: StateFlow<SnackBarEvent> = _snackBarEvent

        fun onModeSelected(mode: MonitorMode) {
            viewModelScope.launch {
                _snackBarEvent.value =
                    when (val outcome = startMonitoring(mode, System.currentTimeMillis())) {
                        is StartOutcome.Monitoring -> {
                            SnackBarEvent.Success("Monitoring ${outcome.mode.machineLabel}s for 1 hour")
                        }

                        is StartOutcome.AlreadyAvailable -> {
                            SnackBarEvent.Success("A ${outcome.mode.machineLabel} is already available!")
                        }

                        is StartOutcome.Failed -> {
                            SnackBarEvent.Error(outcome.message)
                        }

                        StartOutcome.Stopped -> {
                            SnackBarEvent.None
                        }
                    }
            }
        }

        fun resetSnackBarEvent() {
            _snackBarEvent.value = SnackBarEvent.None
        }
    }
