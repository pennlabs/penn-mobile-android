package com.pennapps.labs.pennmobile.laundry.domain.usecase

import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import com.pennapps.labs.pennmobile.laundry.work.LaundryMonitorScheduler
import javax.inject.Inject

class StopLaundryMonitorUseCase
    @Inject
    constructor(
        private val repo: LaundryMonitorRepo,
        private val scheduler: LaundryMonitorScheduler,
    ) {
        operator fun invoke() {
            repo.setMonitorMode(MonitorMode.OFF)
            scheduler.cancelPolling()
        }
    }
