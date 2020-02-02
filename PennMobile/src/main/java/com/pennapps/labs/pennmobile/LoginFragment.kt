package com.pennapps.labs.pennmobile

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.User
import android.webkit.ValueCallback
import com.pennapps.labs.pennmobile.api.Platform.*
import org.apache.commons.lang3.RandomStringUtils
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var cancelButton: Button
    lateinit var user: User
    private lateinit var mLabs: Labs
    lateinit var sp: SharedPreferences
    var loginURL = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do"
    lateinit var codeChallenge: String
    lateinit var platformAuthUrl: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        sp = PreferenceManager.getDefaultSharedPreferences(activity)
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        arguments?.let {
            user = arguments?.getSerializable("user") as User
        }

        codeChallenge = getCodeChallenge(codeVerifier)
        platformAuthUrl = baseUrl + "/accounts/authorize/?response_type=code&client_id=" +
                clientID + "&redirect_uri=" + redirectUri +
                "&code_challenge_method=S256" +
                "&code_challenge=" + codeChallenge +
                "&scope=read+introspection&state="
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        cancelButton = view.findViewById(R.id.cancel_button)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (Build.VERSION.SDK_INT > 19){
                    webView.evaluateJavascript("document.getElementById('pennname').value;", ValueCallback<String> { s ->
                        Log.d("LogName", s) // Prints: "this"
                    })
                    if (url == "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do") {
                        var sessionid = ""
                        val cookies = CookieManager.getInstance().getCookie(url).split(";")
                        for (cookie in cookies){
                            if (cookie.take(12) == " JSESSIONID=") {
                                sessionid = cookie.substring(12)
                                break
                            }
                        }
                        activity?.let { activity ->
                            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                            val editor = sp.edit()
                            editor.putString(getString(R.string.login_SessionID), sessionid)
                            editor.apply()
                        }
                    }
                }

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        //webView.loadUrl(loginURL);
        webView.loadUrl(platformAuthUrl)
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(MyWebViewClient())
        //webView.addJavascriptInterface(JavaScriptInterface(), JAVASCRIPT_OBJ)
        cancelButton.setOnClickListener {
            val fragmentTx = activity!!.supportFragmentManager.beginTransaction()
            fragmentTx.remove(this).commit()
        }
        obtainJavascriptInfo()
    }
    override fun onDestroy() {
        webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
    private fun obtainJavascriptInfo() {
        webView.loadUrl("javascript: " +
                "window.androidObj.userToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".set_username(message) };" +
                "window.androidObj.passwordToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".set_password(message) };" +
                "document.getElementById('submit2').addEventListener('click', function() {" +
                "window.androidObj.userToAndroid('document.getElementById('pennname').value');" +
                "window.androidObj.passwordToAndroid('document.getElementById('password').value');});"
        )
    }
    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun set_username(user: String) {
            activity?.let { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                val editor = sp.edit()
                editor.putString("pennkey", user)
                editor.apply()
            }
        }
        fun set_password(pass: String) {
            activity?.let { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                val editor = sp.edit()
                editor.putString("password", pass)
                editor.apply()
            }    }
        fun set_pennid(id: String) {
            activity?.let { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                val editor = sp.edit()
                editor.putString("pennid", id)
                editor.apply()
            }
        }
    }
    companion object {
        private val JAVASCRIPT_OBJ = "javascript_obj"
    }
}
class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        Log.d("URL OVERRIDING", url)
        if (url.contains("callback")) {
            val urlArr = url.split("?code=").toTypedArray()
            val authCode = urlArr[urlArr.size - 1]
            Log.d("Accounts", authCode)
            getUser(authCode)
        }
        if ((url.contains("execution") && url.contains("weblogin"))){
            //save pennkey and password here
        }
        return super.shouldOverrideUrlLoading(view, url)
    }
}

private fun getUser(authCode: String) {
    val mPlatform = MainActivity.getPlatformInstance()
    Log.d("Accounts", codeVerifier)
    mPlatform.getAccessToken(authCode, "authorization_code", clientID,
            redirectUri, codeVerifier,
            object : ResponseCallback() {
                override fun success(response: Response) {
                    Log.d("Accounts", "access token: " + response.body)

                    mPlatform.getUser("test_access_token", object : ResponseCallback() {
                        override fun success(response: Response) {
                            Log.d("Accounts", "user: " + response.body)
                        }

                        override fun failure(error: RetrofitError) {
                            Log.d("Accounts", "Error getting user $error")
                        }
                    })
                }

                override fun failure(error: RetrofitError) {
                    Log.d("Accounts", "Error fetching access token $error")
                }
            })
}

private fun getCodeChallenge(codeVerifier: String) : String {

    Log.d("Accounts", "code verifier: " + codeVerifier)
    val digest = MessageDigest.getInstance("SHA-256")
    digest.reset()
    val byteArr = digest.digest(codeVerifier.toByteArray())
    val codeChallenge = BigInteger(1, byteArr).toString(16)
    Log.d("Accounts", "code challenge: " + codeChallenge)

    return codeChallenge
}