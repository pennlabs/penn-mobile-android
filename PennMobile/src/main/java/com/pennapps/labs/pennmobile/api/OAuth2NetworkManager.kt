package com.pennapps.labs.pennmobile.api

import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

class OAuth2NetworkManager(mActivity: MainActivity) {

    private var mPlatform = MainActivity.getPlatformInstance()
    private var mActivity = mActivity
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
            calendar.time = Date()
            if (seconds != null) {
                calendar.add(Calendar.SECOND, seconds)
            }

            if (calendar.time <= Date()) { // if it has expired, refresh access token
                refreshAccessToken()
            }
        } else {
            mActivity.startLoginFragment()
        }
    }

    private fun refreshAccessToken() {
        val refreshToken = sp.getString(mActivity.getString(R.string.refresh_token), "")
        val clientID = sp.getString(mActivity.getString(R.string.clientID), "")

        mPlatform.refreshAccessToken(refreshToken,
                "refresh_token", clientID,
                object : Callback<AccessTokenResponse> {

                    override fun success(t: AccessTokenResponse?, response: Response?) {
                        if (response?.status == 200) {
                            val editor = sp.edit()
                            editor.putString(mActivity.getString(R.string.access_token), t?.accessToken)
                            editor.putString(mActivity.getString(R.string.refresh_token), t?.refreshToken)
                            editor.putString(mActivity.getString(R.string.expires_in), t?.expiresIn)
                            editor.apply()
                        }
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error refreshing access token $error")
                        mActivity.startLoginFragment()
                    }
                })
    }


}