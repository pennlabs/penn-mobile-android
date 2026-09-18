package com.pennapps.labs.pennmobile.laundry.notification

interface LaundryNotifier {
    /** Fired by the per-machine alarm the user sets on a specific washer or dryer. */
    fun notifyMachineAvailable(
        roomName: String,
        machineType: String,
        machineId: Int,
    )

    /** Fired by the background monitor watching all of the user's favourite rooms. */
    fun notifyMonitoredMachineAvailable(machineLabel: String)
}
