package com.pennapps.labs.pennmobile.api

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.classes.AuthEvent
import com.pennapps.labs.pennmobile.di.MainScope
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
    @MainScope private val mainScope: CoroutineScope,
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

        mainScope.launch {
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
