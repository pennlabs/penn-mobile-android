package com.pennapps.labs.pennmobile.api

import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.HomeFragment
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
            val expiresAt = sp.getLong(mActivity.getString(R.string.token_expires_at), 0)
            val currentTime = System.currentTimeMillis()
            if (currentTime >= expiresAt) { // if it has expired, refresh access token
                refreshAccessToken()
            }
        } else {
            refreshAccessToken()
        }
    }

    fun getAccessToken(listener: HomeFragment.HomePageListener) {
        val expiresIn = sp.getString(mActivity.getString(R.string.expires_in), "")
        if (expiresIn != "") {
            val expiresAt = sp.getLong(mActivity.getString(R.string.token_expires_at), 0)
            val currentTime = System.currentTimeMillis()
            if (currentTime >= expiresAt) { // if it has expired, refresh access token
                Log.i("Accounts", "Expired")
                refreshAccessToken(listener)
            } else {
                Log.i("Accounts", "Not Expired")
            }
        } else {
            refreshAccessToken(listener)
        }
    }

    private fun refreshAccessToken(listener: HomeFragment.HomePageListener) {
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
                        val expiresInInt = (expiresIn!!.toInt() * 1000)
                        editor.putLong(mActivity.getString(R.string.token_expires_at), System.currentTimeMillis() + expiresInInt)
                        editor.apply()
                        listener.homepage()
                        Log.i("Accounts", "Reloaded Homepage")
                    }
                }

                override fun failure(error: RetrofitError) {
                    Log.e("Accounts", "Error refreshing access token $error", error)
                    val expiresAt = sp.getLong(mActivity.getString(R.string.token_expires_at), 0)
                    if(System.currentTimeMillis() - expiresAt > 6.912e+9) {
                        val editor = sp.edit()
                        editor.remove(mActivity.getString(R.string.penn_password))
                        editor.remove(mActivity.getString(R.string.penn_user))
                        editor.remove(mActivity.getString(R.string.first_name))
                        editor.remove(mActivity.getString(R.string.last_name))
                        editor.remove(mActivity.getString(R.string.email_address))
                        editor.remove(mActivity.getString(R.string.pennkey))
                        editor.remove(mActivity.getString(R.string.accountID))
                        editor.remove(mActivity.getString(R.string.access_token))
                        editor.remove(mActivity.getString(R.string.expires_in))
                        editor.remove(mActivity.getString(R.string.token_expires_at))
                        editor.remove(mActivity.getString(R.string.guest_mode))
                        editor.remove(mActivity.getString(R.string.campus_express_token))
                        editor.remove(mActivity.getString(R.string.campus_token_expires_in))
                        editor.apply()
                        mActivity.startLoginFragment()
                    }
                }
            })
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
                            val expiresInInt = (expiresIn!!.toInt() * 1000)
                            editor.putLong(mActivity.getString(R.string.token_expires_at), System.currentTimeMillis() + expiresInInt)
                            editor.apply()
                        }
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error refreshing access token $error", error)
                        val expiresAt = sp.getLong(mActivity.getString(R.string.token_expires_at), 0)
                        if(System.currentTimeMillis() - expiresAt > 6.912e+9) {
                            val editor = sp.edit()
                            editor.remove(mActivity.getString(R.string.penn_password))
                            editor.remove(mActivity.getString(R.string.penn_user))
                            editor.remove(mActivity.getString(R.string.first_name))
                            editor.remove(mActivity.getString(R.string.last_name))
                            editor.remove(mActivity.getString(R.string.email_address))
                            editor.remove(mActivity.getString(R.string.pennkey))
                            editor.remove(mActivity.getString(R.string.accountID))
                            editor.remove(mActivity.getString(R.string.access_token))
                            editor.remove(mActivity.getString(R.string.expires_in))
                            editor.remove(mActivity.getString(R.string.token_expires_at))
                            editor.remove(mActivity.getString(R.string.guest_mode))
                            editor.remove(mActivity.getString(R.string.campus_express_token))
                            editor.remove(mActivity.getString(R.string.campus_token_expires_in))
                            editor.apply()
                            mActivity.startLoginFragment()
                        }
                    }
                })
    }


}