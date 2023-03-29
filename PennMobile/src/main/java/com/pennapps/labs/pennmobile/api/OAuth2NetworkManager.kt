package com.pennapps.labs.pennmobile.api

import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

class OAuth2NetworkManager(private var mActivity: MainActivity) {

    private var mPlatform = MainActivity.platformInstance
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor = sp?.edit()

    fun getDeviceId() : String {
        val deviceID = Settings.Secure.getString(mActivity.contentResolver, Settings.Secure.ANDROID_ID) ?: "test"
        return deviceID
    }

    fun getAccessToken() {
        val expiresIn = sp.getString(mActivity.getString(R.string.expires_in), "")
        if (expiresIn != "") {
            val seconds = expiresIn?.toInt()
            val calendar = Calendar.getInstance()
            val expiresAt = Date(sp.getLong(mActivity.getString(R.string.token_generated), 0))
            calendar.time = Date()
            if (seconds != null) {
                calendar.add(Calendar.SECOND, -seconds)
            }
            if (calendar.time.after(expiresAt)) { // if it has expired, refresh access token
                refreshAccessToken()
            }
        }
    }

    private fun refreshAccessToken() {
        val refreshToken = sp.getString(mActivity.getString(R.string.refresh_token), "")
        val clientID = BuildConfig.PLATFORM_CLIENT_ID

        mPlatform.refreshAccessToken(refreshToken,
                "refresh_token", clientID,
                object : Callback<AccessTokenResponse> {

                    override fun success(t: AccessTokenResponse?, response: Response?) {
                        if (response?.status == 200) {
                            val editor = sp.edit()
                            editor.putString(mActivity.getString(R.string.access_token), t?.accessToken)
                            editor.putString(mActivity.getString(R.string.refresh_token), t?.refreshToken)
                            editor.putString(mActivity.getString(R.string.expires_in), t?.expiresIn)
                            val calendar = Calendar.getInstance()
                            calendar.time = Date()
                            val expiresIn = t?.expiresIn
                            val expiresInInt = expiresIn!!.toInt()
                            val date = Date(System.currentTimeMillis().plus(expiresInInt)) //or simply new Date();
                            editor.putLong(mActivity.getString(R.string.token_generated), date.time)
                            editor.apply()
                        }
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error refreshing access token $error")
                        // mActivity.startLoginFragment()
                    }
                })
    }


}
