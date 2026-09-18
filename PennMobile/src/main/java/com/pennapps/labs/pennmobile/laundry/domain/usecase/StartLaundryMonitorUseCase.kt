package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.StartOutcome
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.work.LaundryMonitorScheduler
import javax.inject.Inject

class StartLaundryMonitorUseCase
    @Inject
    constructor(
        private val repo: LaundryMonitorRepo,
        private val scheduler: LaundryMonitorScheduler,
        private val stopMonitoring: StopLaundryMonitorUseCase,
    ) {
        suspend operator fun invoke(
            mode: MonitorMode,
            startedAtMillis: Long,
        ): StartOutcome {
            if (mode == MonitorMode.OFF) {
                stopMonitoring()
                return StartOutcome.Stopped
            }

            // Fails closed: never report "monitoring" for a run that was not actually scheduled.
            return when (val availability = repo.isMachineAvailable(mode)) {
                is Result.Error -> {
                    stopMonitoring()
                    StartOutcome.Failed(availability.message)
                }

                is Result.Success -> {
                    if (availability.data) {
                        stopMonitoring()
                        StartOutcome.AlreadyAvailable(mode)
                    } else {
                        repo.setMonitorMode(mode)
                        scheduler.startPolling(mode, startedAtMillis)
                        StartOutcome.Monitoring(mode)
                    }
                }
            }
        }
    }
