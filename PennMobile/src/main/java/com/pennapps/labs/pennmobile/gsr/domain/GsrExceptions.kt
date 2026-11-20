package com.pennapps.labs.pennmobile.gsr.domain

import java.io.IOException

/** Message matches what booking showed before these types existed, so existing toasts don't change. */
class GsrNotLoggedInException : Exception("Authentication failed. Please log in again.")

class GsrOfflineException(
    cause: IOException? = null,
) : IOException("You're offline. Check your connection and try again.", cause)

/** Carries the backend's `{"error": ...}` reason, e.g. "Error: Not enough credits". */
class GsrApiException(
    message: String,
) : Exception(message)
