package com.pennapps.labs.pennmobile.api

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.classes.AccessToken
import com.pennapps.labs.pennmobile.api.classes.AuthEvent
import com.pennapps.labs.pennmobile.di.AppScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hands out access tokens and refreshes them when they expire.
 *
 * Must stay a singleton: each refresh returns a new refresh token, so two instances refreshing in
 * parallel send the same old token, the second gets a 400, and the user is logged out. One instance means
 * one [tokenMutex], so the second caller waits and reuses the first caller's new token.
 */
@Singleton
class OAuth2NetworkManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        @AppScope private val appScope: CoroutineScope,
        private val sp: SharedPreferences,
        private val mStudentLife: StudentLife,
    ) {
        val editor: SharedPreferences.Editor = sp.edit()

        private val _authEvents = MutableSharedFlow<AuthEvent>()
        val authEvents: SharedFlow<AuthEvent> = _authEvents

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
        suspend fun getAccessToken(): String? = (getAccessTokenResult() as? AccessToken.Valid)?.token

        /**
         * Like [getAccessToken], but says why no token is available.
         *
         * A refresh that fails because the device is offline returns [AccessToken.NetworkUnavailable]
         * and does not emit [AuthEvent.RequiresLogin], so offline users aren't sent to the login screen.
         */
        suspend fun getAccessTokenResult(): AccessToken {
            val guestMode = sp.getBoolean(context.getString(R.string.guest_mode), false)
            if (guestMode) {
                return AccessToken.Guest
            }

            // Use a mutex to ensure only one thread/coroutine can check/refresh the token at a time.
            tokenMutex.lock()
            try {
                val tokenExpiresAt = sp.getLong(context.getString(R.string.token_expires_at), 0)
                val tokenIsExpired = Calendar.getInstance().timeInMillis >= tokenExpiresAt

                if (tokenIsExpired) {
                    Log.i("Accounts", "Token is expired or missing. Refreshing...")
                    when (refreshAccessToken()) {
                        RefreshResult.Success -> Unit
                        RefreshResult.Rejected -> return AccessToken.LoggedOut
                        RefreshResult.NetworkError -> return AccessToken.NetworkUnavailable
                    }
                } else {
                    Log.i("Accounts", "Token is still valid.")
                }

                val token = sp.getString(context.getString(R.string.access_token), null)
                return if (token.isNullOrBlank()) AccessToken.LoggedOut else AccessToken.Valid(token)
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

            // Every exit must release the mutex, or all later getAccessToken calls wait forever.
            var released = false
            val release = {
                if (!released) {
                    released = true
                    unlockMutex.invoke()
                }
            }

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
                        currentTime + expiresInInt,
                    )
                    editor.apply()

                    release()
                    function.invoke()
                } else {
                    val error = response.errorBody()!!

                    FirebaseCrashlytics.getInstance().recordException(Exception(error.string()))

                    if (response.code() == 400) {
                        _authEvents.emit(AuthEvent.RequiresLogin)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                release()
            }
        }

        /**
         * Refreshes the access token using the stored refresh token. This is a suspend function
         * that performs the network call.
         *
         * Only a rejected refresh emits [AuthEvent.RequiresLogin]; a network failure doesn't, because
         * the stored refresh token may still be valid once the device is back online.
         */
        private suspend fun refreshAccessToken(): RefreshResult {
            val refreshToken = sp.getString(context.getString(R.string.refresh_token), "")
            val clientID = BuildConfig.PLATFORM_CLIENT_ID

            // If there's no refresh token, we can't do anything.
            if (refreshToken.isNullOrEmpty()) {
                _authEvents.emit(AuthEvent.RequiresLogin)
                return RefreshResult.Rejected
            }

            return try {
                val response =
                    mStudentLife.refreshAccessToken(
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
                        currentTime + expiresInMillis,
                    )
                    editor.apply()
                    Log.i("Accounts", "Access token refreshed successfully.")
                    RefreshResult.Success
                } else {
                    // If refresh fails (e.g., 400 Bad Request), the user needs to log in again.
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    FirebaseCrashlytics.getInstance().recordException(Exception("Token refresh failed: $errorBody"))
                    _authEvents.emit(AuthEvent.RequiresLogin)
                    RefreshResult.Rejected
                }
            } catch (e: IOException) {
                Log.w("Accounts", "Token refresh failed: network unavailable", e)
                RefreshResult.NetworkError
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
                // It's safer to assume login is required on any other refresh exception
                _authEvents.emit(AuthEvent.RequiresLogin)
                RefreshResult.Rejected
            }
        }

        private enum class RefreshResult { Success, Rejected, NetworkError }

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
            function: (String) -> Unit,
        ) {
            getAccessToken {
                val bearerToken =
                    "Bearer " +
                        sharedPreferences.getString(
                            context.getString(R.string.access_token),
                            " ",
                        )

                function(bearerToken)
            }
        }
    }
