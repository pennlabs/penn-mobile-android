package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.domain.PollOutcome
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.work.LaundryPollRequest
import javax.inject.Inject

/**
 * One tick of the background monitor.
 *
 * Time is a parameter rather than an injected clock: Dagger ignores Kotlin default arguments, so a
 * `() -> Long` default on the constructor would fail compilation, and passing it in keeps the
 * one-hour window testable without waiting an hour.
 */
class PollLaundryAvailabilityUseCase
    @Inject
    constructor(
        private val repo: LaundryMonitorRepo,
    ) {
        suspend operator fun invoke(
            mode: MonitorMode,
            startedAtMillis: Long,
            nowMillis: Long,
        ): PollOutcome {
            // Guards a stale queued poll firing after the user already turned monitoring off.
            if (mode == MonitorMode.OFF) {
                return PollOutcome.Expired
            }

            // Checked before the network call so an expired run costs nothing.
            if (nowMillis - startedAtMillis >= LaundryPollRequest.MONITOR_WINDOW_MS) {
                return PollOutcome.Expired
            }

            return when (val availability = repo.isMachineAvailable(mode)) {
                is Result.Error -> PollOutcome.Failed(availability.message)
                is Result.Success -> if (availability.data) PollOutcome.Available else PollOutcome.StillBusy
            }
        }
    }
