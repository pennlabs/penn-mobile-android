package com.pennapps.labs.pennmobile.api.fragments

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.classes.Account
import com.pennapps.labs.pennmobile.dining.fragments.DiningInsightsFragment
import kotlinx.coroutines.launch
import org.apache.commons.lang3.RandomStringUtils
import java.security.MessageDigest
import java.util.Base64
import java.util.Date

/**
 * A simple [Fragment] subclass.
 * Use the [CampusExpressLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CampusExpressLoginFragment : Fragment() {
    lateinit var webView: WebView
    lateinit var headerLayout: LinearLayout
    lateinit var cancelButton: Button
    lateinit var user: Account
    private var mCampusExpress: CampusExpress? = null
    private lateinit var mActivity: MainActivity
    lateinit var sp: SharedPreferences
    lateinit var codeChallenge: String
    lateinit var codeVerifier: String
    lateinit var state: String
    lateinit var campusExpressAuthUrl: String
    lateinit var clientID: String
    lateinit var redirectUri: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_campus_express_login, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCampusExpress = MainActivity.campusExpressInstance
        mActivity = activity as MainActivity
        sp = PreferenceManager.getDefaultSharedPreferences(mActivity)

        // These values are added to the BuildConfig at runtime, to allow GitHub Actions
        // to build the app without pushing the secrets to GitHub
        clientID = "5c09c08b240a56d22f06b46789d0528a"
        redirectUri = "https://pennlabs.org/pennmobile/android/campus_express_callback/"
        codeVerifier = RandomStringUtils.randomAlphanumeric(64)
        codeChallenge = getCodeChallenge(codeVerifier)
        state = getStateString()
        campusExpressAuthUrl = "https://prod.campusexpress.upenn.edu/api/v1/oauth/authorize"
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        headerLayout = view.findViewById(R.id.linear_layout)
        cancelButton = view.findViewById(R.id.cancel_button)
        val uri =
            Uri
                .parse(campusExpressAuthUrl)
                .buildUpon()
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", clientID)
                .appendQueryParameter("state", state)
                .appendQueryParameter("scope", "read")
                .appendQueryParameter("code_challenge", codeChallenge)
                .appendQueryParameter("code_challenge_method", "S256")
                .appendQueryParameter("redirect_uri", redirectUri)
                .build()
        webView.loadUrl(uri.toString())
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = MyWebViewClient()

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?,
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            view?.visibility = View.INVISIBLE
            headerLayout.visibility = View.INVISIBLE
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            url: String,
        ): Boolean {
            if (url.contains("callback") && url.contains("?code=")) {
                val urlArr = url.split("?code=", "&state=").toTypedArray()
                val authCode = urlArr[1]
                val clientState = urlArr[2]
                initiateAuthentication(authCode)
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

    private fun goToDiningInsights(refresh: Boolean) {
        if (refresh) {
            val fragment = DiningInsightsFragment()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.campus_express_page, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initiateAuthentication(authCode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            mCampusExpress?.let {
                try {
                    val response =
                        it.getAccessToken(
                            authCode,
                            "authorization_code",
                            clientID,
                            redirectUri,
                            codeVerifier,
                        )

                    if (response.isSuccessful) {
                        val t = response.body()
                        val accessToken = t?.accessToken
                        val expiresIn = t?.expiresIn
                        val editor = sp.edit()
                        if (accessToken != null) {
                            editor.putString(mActivity.getString(R.string.campus_express_token), accessToken)
                        }
                        if (expiresIn != null) {
                            val currentDate = Date()
                            currentDate.time += (expiresIn * 1000)
                            val expiresAt = currentDate.time
                            editor.putLong(mActivity.getString(R.string.campus_token_expires_in), expiresAt)
                        }
                        editor.apply()
                        goToDiningInsights(true)
                    } else {
                        val error = Exception(response.errorBody()?.string() ?: "Unknown Error")
                        Log.e("Campus Webview", "Error fetching access token $error")
                        Toast
                            .makeText(
                                context,
                                "Error getting campus express authorization",
                                Toast.LENGTH_SHORT,
                            ).show()
                        goToDiningInsights(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getCodeChallenge(codeVerifier: String): String {
        // Hash the code verifier
        val md = MessageDigest.getInstance("SHA-256")
        val byteArr = md.digest(codeVerifier.toByteArray())

        // Base-64 encode
        var codeChallenge =
            Base64.getEncoder().encodeToString(byteArr)

        // Replace characters to make it web safe
        codeChallenge = codeChallenge.replace("=", "")
        codeChallenge = codeChallenge.replace("+", "-")
        codeChallenge = codeChallenge.replace("/", "_")

        return codeChallenge
    }

    private fun getStateString(): String {
        var stateString = RandomStringUtils.randomAlphanumeric(64)

        // Replace characters to make it web safe
        stateString = stateString.replace("=", "")
        stateString = stateString.replace("+", "-")
        stateString = stateString.replace("/", "_")

        return stateString
    }
}
