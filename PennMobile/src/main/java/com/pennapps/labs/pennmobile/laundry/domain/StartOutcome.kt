package com.pennapps.labs.pennmobile.laundry.domain

/** The result of the user asking to start monitoring. */
sealed interface StartOutcome {
    data class Monitoring(
        val mode: MonitorMode,
    ) : StartOutcome

    /** A machine was already free, so there was nothing to wait for. */
    data class AlreadyAvailable(
        val mode: MonitorMode,
    ) : StartOutcome

    data object Stopped : StartOutcome

    data class Failed(
        val message: String,
    ) : StartOutcome
}
