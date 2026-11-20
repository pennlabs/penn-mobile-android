package com.pennapps.labs.pennmobile.api.classes

/**
 * Outcome of asking [com.pennapps.labs.pennmobile.api.OAuth2NetworkManager] for an access token.
 *
 * [NetworkUnavailable] is kept separate from [LoggedOut] so callers can show "you're offline"
 * instead of sending a signed-in user back to the login screen.
 */
sealed interface AccessToken {
    data class Valid(
        val token: String,
    ) : AccessToken

    data object Guest : AccessToken

    data object LoggedOut : AccessToken

    data object NetworkUnavailable : AccessToken
}
