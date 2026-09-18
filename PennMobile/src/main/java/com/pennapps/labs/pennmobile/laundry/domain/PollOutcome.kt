package com.pennapps.labs.pennmobile.laundry.domain

/** What a single availability poll found. Reports facts; the caller decides what to do about them. */
sealed interface PollOutcome {
    data object Available : PollOutcome

    data object StillBusy : PollOutcome

    /** The monitoring window elapsed without finding a free machine. */
    data object Expired : PollOutcome

    data class Failed(
        val message: String,
    ) : PollOutcome
}
