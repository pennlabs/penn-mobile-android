package com.pennapps.labs.pennmobile.api

import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.pennapps.labs.pennmobile.LoginWebviewFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import retrofit.ResponseCallback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.android.AndroidLog
import retrofit.client.Response
import retrofit.converter.GsonConverter

class PennAuthRequestable (mActivity: MainActivity) {

    private var mPlatform = MainActivity.getPlatformInstance()
    private var mLabs = MainActivity.getLabsInstance()
    private var mActivity = mActivity
    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    val editor = sp.edit()

    val pennkeyLoginBaseUrl : String = "https://weblogin.pennkey.upenn.edu"
    val authUrl : String = "https://weblogin.pennkey.upenn.edu/idp/profile"

    val pennInTouchBaseUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do"
    val pennInTouchDegreeUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do?fastStart=mobileAdvisors"
    val pennInTouchShibbolethUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do/Shibboleth.sso/SAML2/POST"

    /**
     * Created by Marta on 3/10/2020.
     * make request, if responseUrl == targetUrl set shibboleth logged in to true in shared prefs
     * else if responseUrl.contains(authUrl) then makeRequestWithAuth
     * else makeRequestWithShibboleth
     */

    fun makeAuthRequest(baseUrl: String, target: String, shibbolethUrl: String, callback: (Response?, Error?) -> Unit) {

        val mInstance = getInstanceFromBaseUrl(baseUrl)
        val targetUrl = "$baseUrl/$target"

        mInstance.makeAuthRequest(target).subscribe({ response ->
            val responseUrl = response.url
            when {
                responseUrl == targetUrl -> {
                    editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), true)
                    callback(response, null)
                }
                responseUrl.contains(authUrl) -> {
                    makeRequestWithAuth(targetUrl, shibbolethUrl, response.body.toString(), callback)
                }
                else -> {
                    makeRequestWithShibboleth(targetUrl, shibbolethUrl, response.body.toString(), callback)
                }
            }

        }, { throwable ->
            editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
            Log.e("Accounts", "make auth request error " + throwable)
            callback(null, Error("Error making auth request"))
        })
        // store cookies
    }

    private fun makeRequestWithAuth(targetUrl: String, shibbolethUrl: String, html: String,
                                    callback: (Response?, Error?) -> Unit) {

        // if it doesn't match the form <form action="some action url" method="POST" id="login-form">, send error

        val actionUrl = html.substringAfter("<form action=\"", "")
                .substringBefore( "\" method=\"POST\" id=\"login-form\">", "")
        if (actionUrl == "") {
            callback(null, Error("Error making request with auth"))
        }

        val password = LoginWebviewFragment().getDecodedPassword(mActivity)
        val pennkey = sp.getString(mActivity.getString(R.string.pennkey), null)

        if (password == null || pennkey == null) {
            editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
            Log.e("Accounts", "Missing pennkey or password")
            callback(null, Error("Error making request with auth"))
            return
        }

        val url = pennkeyLoginBaseUrl + actionUrl

        val mInstance = getInstanceFromBaseUrl(url)

        // make post request with params, when done either two step or makeRequestWithShibboleth
        mInstance.makeRequestWithAuth(pennkey, password, "", object : ResponseCallback() {
            override fun success(response: Response) {
                Log.d("Accounts", "make request with auth response url " + response.url)
                makeRequestWithShibboleth(targetUrl, shibbolethUrl, response.body.toString(), callback)
            }

            override fun failure(error: RetrofitError) {
                editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                Log.e("Accounts", "Error making request with auth " + error)
            }
        })

    }

    private fun makeRequestWithShibboleth(targetUrl: String, shibbolethUrl: String, html: String,
                                          callback: (Response?, Error?) -> Unit) {

//        guard let samlResponse = html.getMatches(for: "<input type=\"hidden\" name=\"SAMLResponse\" value=\"(.*?)\"/>").first,
//        let relayState = html.getMatches(for: "<input type=\"hidden\" name=\"RelayState\" value=\"(.*?)\"/>").first?.replacingOccurrences(of: "&#x3a;", with: ":") else {
//            UserDefaults.standard.setShibbolethAuth(authedIn: false)
//            completionHandler(nil, nil, NetworkingError.authenticationError)
//            return
//        }

        val samlResponse = html.substringAfter("<input type=\"hidden\" name=\"SAMLResponse\" value=\"", "")
                .substringBefore( "\"/>", "")
        val relayState = html.substringAfter("<input type=\"hidden\" name=\"RelayState\" value=\"", "")
                .substringBefore("\"/>", "").replace("&#58;", ":")
        if (samlResponse == "" || relayState == "") {
            callback(null, Error("Error making request with auth"))
            return
        }

        val mInstance = getInstanceFromBaseUrl(shibbolethUrl)

        mInstance.makeRequestWithShibboleth(relayState, samlResponse, object : ResponseCallback() {
            override fun success(response: Response) {

                if (response.url == targetUrl) {
                    editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), true)
                    callback(response, null)
                } else {
                    callback(null, Error("Error making request with shibboleth"))
                }
            }

            override fun failure(error: RetrofitError) {
                editor.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                Log.e("Accounts", "Error making request with shibboleth " + error)
            }
        })
    }

    companion object {
        fun getInstanceFromBaseUrl(baseUrl : String): PennAuthRequestableInterface {
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()

            val restAdapter = RestAdapter.Builder()
                    .setConverter(GsonConverter(gson))
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(AndroidLog("PennAuthRequestable"))
                    .setEndpoint(baseUrl)
                    .build()

            return restAdapter.create(PennAuthRequestableInterface::class.java)
        }
    }
}