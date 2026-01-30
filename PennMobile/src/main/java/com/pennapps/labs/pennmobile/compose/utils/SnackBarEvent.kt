package com.pennapps.labs.pennmobile.compose.utils

/**
 * Represents a one-time event for showing a SnackBar in the UI.
 * This sealed class ensures that all possible event states are handled explicitly.
 */
sealed class SnackBarEvent(
    open val message: String?,
) {
    /** A successful operation that should be communicated to the user. */
    data class Success(
        override val message: String,
    ) : SnackBarEvent(message)

    /** An error or failure that should be communicated to the user. */
    data class Error(
        override val message: String,
    ) : SnackBarEvent(message)

    /** The default, empty state where no SnackBar should be shown. */
    data object None : SnackBarEvent(null)
}
