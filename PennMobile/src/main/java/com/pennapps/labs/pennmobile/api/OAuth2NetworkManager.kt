package com.pennapps.labs.pennmobile.api

import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.classes.AccessTokenResponse
import kotlinx.coroutines.launch
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.Calendar

class OAuth2NetworkManager(
    private var mActivity: MainActivity,
) {
    private var mStudentLifeRf2 = MainActivity.studentLifeInstanceRf2
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor = sp?.edit()

    fun getDeviceId(): String {
        val deviceID = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID) ?: "test"
        return deviceID
    }

    @Synchronized
    fun getAccessToken(function: () -> Unit) {
        // if guest mode, then just do network request dangerously(TEMPORARY FIX, PLEASE DO SOMETHING
        // ABOUT THIS IN FUTURE)
        val guestMode = sp.getBoolean(mActivity.getString(R.string.guest_mode), false)
        if (guestMode) {
            function.invoke()
            return
        }

        mActivity.lifecycleScope.launch {
            val tokenMutex = mActivity.tokenMutex
            tokenMutex.lock()
            val expiresIn = sp.getString(mActivity.getString(R.string.expires_in), "")
            if (expiresIn != "") {
                val expiresAt = sp.getLong(mActivity.getString(R.string.token_expires_at), 0)
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
        val refreshToken = sp.getString(mActivity.getString(R.string.refresh_token), "") ?: ""
        val clientID = BuildConfig.PLATFORM_CLIENT_ID

        val response = mStudentLifeRf2.refreshAccessToken(
            refreshToken,
            "refresh_token",
            clientID,
        )

        if (response.isSuccessful) {
            val t = response.body()!!

            val editor = sp.edit()
            editor.putString(mActivity.getString(R.string.access_token), t.accessToken)
            editor.putString(mActivity.getString(R.string.refresh_token), t.refreshToken)
            editor.putString(mActivity.getString(R.string.expires_in), t.expiresIn)
            val expiresIn = t.expiresIn
            val expiresInInt = (expiresIn!!.toInt() * 1000)
            val currentTime = Calendar.getInstance().timeInMillis
            editor.putLong(mActivity.getString(R.string.token_expires_at), currentTime + expiresInInt)
            editor.apply()
            Log.i("Accounts", "Refreshed access token")

            unlockMutex.invoke()
            function.invoke()
        } else {
            val error = response.errorBody()!!

            FirebaseCrashlytics.getInstance().recordException(Exception(error.toString()))
            Log.e("Accounts", "Error refreshing access token $error")

            if (response.code() == 400) {
                mActivity.startLoginFragment()
                unlockMutex.invoke()
            }
        }
    }
}
