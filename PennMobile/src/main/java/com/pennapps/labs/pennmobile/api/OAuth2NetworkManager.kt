package com.pennapps.labs.pennmobile.api

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.classes.AuthEvent
import com.pennapps.labs.pennmobile.di.AppScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.Calendar
import javax.inject.Inject

class OAuth2NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @AppScope private val appScope: CoroutineScope,
    private val sp: SharedPreferences,
    private val mStudentLife: StudentLife,
) {
    val editor: SharedPreferences.Editor = sp.edit()

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvent: SharedFlow<AuthEvent> = _authEvents

    private val tokenMutex = Mutex()

    fun getDeviceId(): String {
        val deviceID =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "test"
        return deviceID
    }

    fun getAccessToken(function: () -> Unit) {
        // if guest mode, then just do network request dangerously(TEMPORARY FIX, PLEASE DO SOMETHING
        // ABOUT THIS IN FUTURE)
        val guestMode = sp.getBoolean(context.getString(R.string.guest_mode), false)
        if (guestMode) {
            function.invoke()
            return
        }

        appScope.launch {
            tokenMutex.lock()

            val expiresIn = sp.getString(context.getString(R.string.expires_in), "")
            if (expiresIn != "") {
                val expiresAt = sp.getLong(context.getString(R.string.token_expires_at), 0)
                val currentTime = Calendar.getInstance().timeInMillis
                if (currentTime >= expiresAt) { // if it has expired, refresh access token
                    Log.i("Accounts", "Expired")
                    refreshAccessToken(function) {
                        tokenMutex.unlock()
                    }
                } else {
                    Log.i("Accounts", "Not Expired")
                    tokenMutex.unlock()
                    function.invoke()
                }
            } else {
                refreshAccessToken(function) {
                    tokenMutex.unlock()
                }
            }
        }
    }

    /**
     * TODO: Remove the old implementation of getAccessToken() once this is fully integrated in the app.
     * Retrieves a valid access token, refreshing it if necessary. This function suspends until
     * a token is available or authentication fails.
     *
     * @return The valid access token as a String, or `null` if the user is in guest mode
     * or if authentication fails.
     */
    suspend fun getAccessToken(): String? {
        val guestMode = sp.getBoolean(context.getString(R.string.guest_mode), false)
        if (guestMode) {
            // It's better to explicitly return null than to proceed insecurely.
            return null
        }

        // Use a mutex to ensure only one thread/coroutine can check/refresh the token at a time.
        tokenMutex.lock()
        try {
            val tokenExpiresAt = sp.getLong(context.getString(R.string.token_expires_at), 0)
            val tokenIsExpired = Calendar.getInstance().timeInMillis >= tokenExpiresAt

            if (tokenIsExpired) {
                Log.i("Accounts", "Token is expired or missing. Refreshing...")
                val refreshSucceeded = refreshAccessToken()
                if (!refreshSucceeded) {
                    // If the refresh failed, we cannot provide a token.
                    return null
                }
            } else {
                Log.i("Accounts", "Token is still valid.")
            }

            // At this point, the token in SharedPreferences is guaranteed to be valid.
            return sp.getString(context.getString(R.string.access_token), null)
        } finally {
            // Crucial: Always unlock the mutex in a finally block to prevent deadlocks.
            tokenMutex.unlock()
        }
    }

    private suspend fun refreshAccessToken(
        function: () -> Unit,
        unlockMutex: () -> Unit,
    ) {
        val refreshToken = sp.getString(context.getString(R.string.refresh_token), "") ?: ""
        val clientID = BuildConfig.PLATFORM_CLIENT_ID

        try {
            val response =
                mStudentLife.refreshAccessToken(
                    refreshToken,
                    "refresh_token",
                    clientID,
                )

            val t = response.body()

            if (response.isSuccessful && t != null) {
                val editor = sp.edit()
                editor.putString(context.getString(R.string.access_token), t.accessToken)
                editor.putString(context.getString(R.string.refresh_token), t.refreshToken)
                editor.putString(context.getString(R.string.expires_in), t.expiresIn)
                val expiresIn = t.expiresIn
                val expiresInInt = (expiresIn!!.toInt() * 1000)
                val currentTime = Calendar.getInstance().timeInMillis
                editor.putLong(
                    context.getString(R.string.token_expires_at),
                    currentTime + expiresInInt
                )
                editor.apply()

                unlockMutex.invoke()
                function.invoke()
            } else {
                val error = response.errorBody()!!

                FirebaseCrashlytics.getInstance().recordException(Exception(error.string()))

                if (response.code() == 400) {
                    _authEvents.emit(AuthEvent.RequiresLogin)
                    unlockMutex.invoke()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Refreshes the access token using the stored refresh token. This is a suspend function
     * that performs the network call.
     *
     * @return `true` if the token was refreshed successfully, `false` otherwise.
     */
    private suspend fun refreshAccessToken(): Boolean {
        val refreshToken = sp.getString(context.getString(R.string.refresh_token), "")
        val clientID = BuildConfig.PLATFORM_CLIENT_ID

        // If there's no refresh token, we can't do anything.
        if (refreshToken.isNullOrEmpty()) {
            _authEvents.emit(AuthEvent.RequiresLogin)
            return false
        }

        return try {
            val response = mStudentLife.refreshAccessToken(
                refreshToken,
                "refresh_token",
                clientID,
            )

            val t = response.body()

            if (response.isSuccessful && t != null) {
                // Save the new token details
                val editor = sp.edit()
                editor.putString(context.getString(R.string.access_token), t.accessToken)
                editor.putString(context.getString(R.string.refresh_token), t.refreshToken)
                editor.putString(context.getString(R.string.expires_in), t.expiresIn)

                val expiresInSeconds = t.expiresIn?.toLongOrNull() ?: 3600L
                val expiresInMillis = expiresInSeconds * 1000
                val currentTime = Calendar.getInstance().timeInMillis
                editor.putLong(
                    context.getString(R.string.token_expires_at),
                    currentTime + expiresInMillis
                )
                editor.apply()
                Log.i("Accounts", "Access token refreshed successfully.")
                true // Return true on success
            } else {
                // If refresh fails (e.g., 400 Bad Request), the user needs to log in again.
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                FirebaseCrashlytics.getInstance().recordException(Exception("Token refresh failed: $errorBody"))
                _authEvents.emit(AuthEvent.RequiresLogin)
                false // Return false on failure
            }
        } catch (e: Exception) {
            // Handle network exceptions during token refresh
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
            // It's safer to assume login is required on any refresh exception
            _authEvents.emit(AuthEvent.RequiresLogin)
            false // Return false on failure
        }
    }

    /**
     * Utils function that removes the need of writing the Bearer Token code over and over again
     * by providing a completely authenticated context
     *
     * @param sharedPreferences -> SharedPreferences need to obtain access token
     * @param context -> Context need to obtain access token string from Resources
     * @param function(string) -> Code to execute using your bearerToken (string).
     */
    fun doWhileAuthenticated(
        sharedPreferences: SharedPreferences,
        context: Context,
        function: (String) -> Unit
    ) {
        getAccessToken {
            val bearerToken =
                "Bearer " + sharedPreferences.getString(
                    context.getString(R.string.access_token),
                    " "
                )

            function(bearerToken)
        }
    }
}
