package com.pennapps.labs.pennmobile.laundry

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import com.pennapps.labs.pennmobile.laundry.repo.LaundryMonitorRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeLaundryMonitorRepo(
    private var availability: Result<Boolean> = Result.Success(false),
) : LaundryMonitorRepo {
    private val _monitorMode = MutableStateFlow(MonitorMode.OFF)
    override val monitorMode: StateFlow<MonitorMode> = _monitorMode

    val modesSet = mutableListOf<MonitorMode>()
    var availabilityChecks = 0
        private set

    override fun setMonitorMode(mode: MonitorMode) {
        modesSet += mode
        _monitorMode.value = mode
    }

    override suspend fun isMachineAvailable(mode: MonitorMode): Result<Boolean> {
        availabilityChecks++
        return availability
    }
}
