package com.pennapps.labs.pennmobile.laundry.repo

import com.pennapps.labs.pennmobile.compose.utils.Result
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode
import kotlinx.coroutines.flow.StateFlow

interface LaundryMonitorRepo {
    val monitorMode: StateFlow<MonitorMode>

    fun setMonitorMode(mode: MonitorMode)

    /**
     * Whether any machine of [mode] is free in the user's favourite rooms.
     *
     * Returns [Result.Error] rather than false when the check could not be made, so an expired
     * token or a dropped connection cannot masquerade as "nothing is available".
     */
    suspend fun isMachineAvailable(mode: MonitorMode): Result<Boolean>
}
