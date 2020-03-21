package com.pennapps.labs.pennmobile.api

import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.LoginWebviewFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


class PennAuthRequestable (private var mActivity: MainActivity, private var baseUrl: String) {

    private val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
    private val mInstance = getInstanceFromBaseUrl(baseUrl)
    val editor: SharedPreferences.Editor? = sp.edit()

    private val pennkeyLoginBaseUrl : String = "https://weblogin.pennkey.upenn.edu"
    private val authUrl : String = "https://weblogin.pennkey.upenn.edu/idp/profile"

    val pennInTouchBaseUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do"
    val pennInTouchDegreeUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do?fastStart=mobileAdvisors"
    val pennInTouchShibbolethUrl = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do/Shibboleth.sso/SAML2/POST"

    /**
     * Created by Marta on 3/10/2020.
     * make request, if responseUrl == targetUrl you're done, set shibboleth logged in to true in shared prefs
     * else if responseUrl.contains(authUrl) then makeRequestWithAuth
     * else makeRequestWithShibboleth
     */

    fun makeAuthRequest(targetUrl: String, shibbolethUrl: String, callback: (Response<String>?, Error?) -> Unit) {

        val call : Call<String> = mInstance.makeAuthRequest(targetUrl)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                // response.isSuccessful is true if the response code is 2xx
                if (response.isSuccessful) {
                    val html = response.body() ?: ""
                    val responseUrl = response.raw().request.url.toString()
                    when {
                        responseUrl == targetUrl -> {
                            editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), true)
                            callback(response, null)
                        }
                        responseUrl.contains(authUrl) -> {
                            makeRequestWithAuth(targetUrl, shibbolethUrl, html, callback)
                        }
                        else -> {
                            makeRequestWithShibboleth(targetUrl, shibbolethUrl, html, callback)
                        }
                    }

                } else {
                    val errorBody = response.errorBody()
                    editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                    Log.e("Accounts", "Make auth request error: $errorBody")
                    callback(null, Error("Error making auth request"))
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                t.printStackTrace()
                callback(null, Error("Error making auth request"))
            }
        })
        // store cookies
    }

    private fun makeRequestWithAuth(targetUrl: String, shibbolethUrl: String, html: String,
                                    callback: (Response<String>?, Error?) -> Unit) {

        // if the html doesn't match <form action="some action url" method="POST" id="login-form">, send error
        val actionUrl = html.substringAfter("<form action=\"", "")
                .substringBefore( "\" method=\"POST\" id=\"login-form\">", "")
        if (actionUrl == "") {
            callback(null, Error("Error making request with auth"))
            //return
        }

        val password = LoginWebviewFragment().getDecodedPassword(mActivity)
        val pennkey = sp.getString(mActivity.getString(R.string.pennkey), null)

        if (password == null || pennkey == null) {
            editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
            Log.e("Accounts", "Missing pennkey or password")
            callback(null, Error("Missing pennkey or password"))
            return
        }

//        val url = targetUrl + actionUrl
        val url = "https://weblogin.pennkey.upenn.edu/idp/profile/SAML2/Redirect/SSO?execution=e1s1"

        // make post request with params, when done either two step or makeRequestWithShibboleth
        val call : Call<String> = mInstance.makeRequestWithAuth(url, pennkey, password, "")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseHtml = response.body() ?: ""
                    if (responseHtml.contains("two-step-form")) {
                        callback(null, Error("Need to two fac"))
                    } else {
                        makeRequestWithShibboleth(targetUrl, shibbolethUrl, responseHtml, callback)
                    }
                } else {
                    val errorBody = response.errorBody()
                    editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                    Log.e("Accounts", "Make request with auth error: $errorBody")
                    callback(null, Error("Error making request with auth"))
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                t.printStackTrace()
                callback(null, Error("Error making request with auth"))
            }
        })

    }

    private fun makeRequestWithShibboleth(targetUrl: String, shibbolethUrl: String, html: String,
                                          callback: (Response<String>?, Error?) -> Unit) {

        val samlResponse = html.substringAfter("<input type=\"hidden\" name=\"SAMLResponse\" value=\"", "")
                .substringBefore( "\"/>", "")
        val relayState = html.substringAfter("<input type=\"hidden\" name=\"RelayState\" value=\"", "")
                .substringBefore("\"/>", "").replace("&#x3a;", ":")
        if (samlResponse == "" || relayState == "") {
            callback(null, Error("Error making request with shibboleth"))
            //return
        }

        val call : Call<String> = mInstance.makeRequestWithShibboleth(shibbolethUrl, relayState, samlResponse)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseUrl = response.raw().request.url.toString()
                    if (responseUrl == targetUrl) {
                        editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), true)
                        callback(response, null)
                    } else {
                        callback(null, Error("Error making request with shibboleth"))
                    }
                } else {
                    val errorBody = response.errorBody()
                    editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                    Log.e("Accounts", "Make request with shibboleth error: $errorBody")
                    callback(null, Error("Error making request with shibboleth"))
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                editor?.putBoolean(mActivity.getString(R.string.shibboleth_authed_in), false)
                t.printStackTrace()
                callback(null, Error("Error making request with shibboleth"))
            }
        })

    }

    companion object {
        fun getInstanceFromBaseUrl(baseUrl : String): PennAuthRequestableInterface {

            // Interceptor to add logging
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)

            val retrofit = Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .build()

            return retrofit.create(PennAuthRequestableInterface::class.java)
        }
    }
}
