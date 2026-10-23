package com.pennapps.labs.pennmobile.gsr.domain

import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation

/**
 * Outcome of a GSR search. Each case needs its own message, so "nothing free", "offline",
 * and "backend error" never collapse into a generic "No results found".
 */
sealed interface GsrSearchResult {
    /** At least one option, earliest first. */
    data class Found(
        val options: List<GsrBookingOption>,
    ) : GsrSearchResult

    /** Signed in and online, but no room is free for the whole window. */
    data object NoneAvailable : GsrSearchResult

    data class DurationTooLong(
        val requestedMinutes: Int,
        val maxMinutes: Int,
        val location: GsrLocation,
    ) : GsrSearchResult

    /** Guest mode, or the session was rejected and the user must sign in again. */
    data object NotLoggedIn : GsrSearchResult

    data object Offline : GsrSearchResult

    /** The backend's own `{"error": ...}` text, or a generic message for unexpected failures. */
    data class Failed(
        val reason: String,
    ) : GsrSearchResult
}
