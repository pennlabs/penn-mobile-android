package com.pennapps.labs.pennmobile.api

import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.GetUserResponse
import com.pennapps.labs.pennmobile.classes.SaveAccountResponse
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.security.MessageDigest
import java.util.*

class OAuth2NetworkManager(private val mActivity: MainActivity) {

    private val mPlatform = MainActivity.getPlatformInstance()
    private val mLabs = MainActivity.getLabsInstance()
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)

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
        val refreshToken = sp.getString(mActivity.getString(R.string.refresh_token), "") ?: ""
        val clientID = sp.getString(mActivity.getString(R.string.clientID), "") ?: ""

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

    fun initiateAuthentication(authCode: String) {
        val clientID = mActivity.getString(R.string.clientID)
        val redirectUri = mActivity.getString(R.string.redirectUri)
        mPlatform.getAccessToken(authCode,
                "authorization_code", clientID, redirectUri, Platform.codeVerifier,
                object : Callback<AccessTokenResponse> {

                    override fun success(t: AccessTokenResponse?, response: Response?) {
                        if (response?.status == 200) {
                            val accessToken = t?.accessToken
                            val editor = sp.edit()
                            editor.putString(mActivity.getString(R.string.access_token), accessToken)
                            editor.putString(mActivity.getString(R.string.refresh_token), t?.refreshToken)
                            editor.putString(mActivity.getString(R.string.expires_in), t?.expiresIn)
                            editor.apply()
                            if (accessToken != null) {
                                getUser(accessToken)
                            }
                        }
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error fetching access token $error")
                    }
                })
    }

    private fun getUser(accessToken: String) {
        mPlatform.getUser("Bearer " + accessToken, accessToken,
                object : Callback<GetUserResponse> {

                    override fun success(t: GetUserResponse?, response: Response?) {
                        val user = t?.user
                        val editor = sp.edit()
                        editor.putString(mActivity.getString(R.string.first_name), user?.firstName)
                        editor.putString(mActivity.getString(R.string.last_name), user?.lastName)
                        editor.putString(mActivity.getString(R.string.email_address), user?.email)
                        editor.putString(mActivity.getString(R.string.pennkey), user?.username)
                        editor.apply()

                        saveAccount(Account(user?.firstName, user?.lastName,
                                user?.username, user?.pennid, user?.email, user?.affiliation))
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error getting user $error")
                    }
                })
    }

    private fun saveAccount(account: Account) {
        mLabs.saveAccount(account, object : Callback<SaveAccountResponse> {

            override fun success(t: SaveAccountResponse?, response: Response?) {
                val editor = sp.edit()
                editor.putString(mActivity.getString(R.string.accountID), t?.accountID)
                editor.apply()
                // After saving the account, go to homepage
                mActivity.startHomeFragment()
            }

            override fun failure(error: RetrofitError) {
                Log.e("Accounts", "Error saving account $error")
            }
        })
    }


    companion object {
        fun getCodeChallenge(codeVerifier: String) : String {

            val digest = MessageDigest.getInstance("SHA-256")
            digest.reset()
            val byteArr = digest.digest(codeVerifier.toByteArray())

            val codeChallenge = android.util.Base64.encodeToString(byteArr, Base64.URL_SAFE)
                    .replace("=", "")

            Log.d("Platform", "code verifier " + codeVerifier)
            Log.d("Platform", "code challenge " + codeChallenge)

            return codeChallenge
        }
    }


}