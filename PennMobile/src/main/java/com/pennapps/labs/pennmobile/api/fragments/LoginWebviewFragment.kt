package com.pennapps.labs.pennmobile.api.fragments

import StudentLife
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pennapps.labs.pennmobile.BuildConfig
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.api.classes.Account
import kotlinx.coroutines.launch
import org.apache.commons.lang3.RandomStringUtils
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Base64
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class LoginWebviewFragment : Fragment() {
    lateinit var webView: WebView
    lateinit var headerLayout: LinearLayout
    lateinit var cancelButton: Button
    lateinit var user: Account
    private lateinit var mStudentLife: StudentLife
    private var mPlatform: Platform? = null
    private lateinit var mActivity: MainActivity
    lateinit var sp: SharedPreferences
    lateinit var codeChallenge: String
    lateinit var codeVerifier: String
    lateinit var platformAuthUrl: String
    lateinit var clientID: String
    lateinit var redirectUri: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_login_webview, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mPlatform = MainActivity.platformInstance2
        arguments?.let {
            user = arguments?.getSerializable("user") as Account
        }
        mActivity = activity as MainActivity
        sp = PreferenceManager.getDefaultSharedPreferences(mActivity)

        // These values are added to the BuildConfig at runtime, to allow GitHub Actions
        // to build the app without pushing the secrets to GitHub
        clientID = BuildConfig.PLATFORM_CLIENT_ID
        redirectUri = BuildConfig.PLATFORM_REDIRECT_URI
        codeVerifier = RandomStringUtils.randomAlphanumeric(64)
        codeChallenge = getCodeChallenge(codeVerifier)
        platformAuthUrl = Platform.platformBaseUrl + "accounts/authorize/?response_type=code&client_id=" +
            clientID + "&redirect_uri=" + redirectUri + "&code_challenge_method=S256" +
            "&code_challenge=" + codeChallenge + "&scope=read+introspection&state="
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        headerLayout = view.findViewById(R.id.linear_layout)
        cancelButton = view.findViewById(R.id.cancel_button)

        webView.loadUrl(platformAuthUrl)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = MyWebViewClient()

        cancelButton.setOnClickListener {
            mActivity.startLoginFragment()
        }
    }

    private fun encryptPassword(password: String) {
        val secretKey = createSecretKey() as SecretKey
        val cipher =
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7,
            )
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encryptionIv = cipher.iv
        val passwordBytes = password.toByteArray(Charset.forName("UTF-8"))
        val encryptedPasswordBytes = cipher.doFinal(passwordBytes)
        val encryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes)

        // save the encrypted password
        val spEditor = sp.edit()
        spEditor.putString("penn_password", encryptedPassword)
        spEditor.apply()
        spEditor.putString("encryptionIv", Base64.getEncoder().encodeToString(encryptionIv))
        spEditor.apply()
    }

    private fun saveUsername(username: String) {
        val editor = sp.edit()
        editor.putString(getString(R.string.penn_user), username)
        editor.apply()
    }

    private fun createSecretKey(): SecretKey? {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder("Key", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build(),
        )
        return keyGenerator.generateKey()
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?,
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            view?.visibility = INVISIBLE
            headerLayout.visibility = INVISIBLE
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            url: String,
        ): Boolean {
            if (url.contains("callback") && url.contains("?code=")) {
                val urlArr = url.split("?code=").toTypedArray()
                val authCode = urlArr[urlArr.size - 1]
                Log.d("AuthCode", authCode)
                Log.d("ClientId", clientID)
                Log.d("RedirectURI", redirectUri)
                Log.d("CodeVerifier", codeVerifier)
                initiateAuthentication(authCode)
            }
            if (url.contains("weblogin") && url.contains("pennkey")) {
                webView.evaluateJavascript("document.getElementById('pennname').value;") { s ->
                    saveUsername(s)
                }
                webView.evaluateJavascript("document.getElementById('password').value;") { s ->
                    encryptPassword(s)
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

    private fun initiateAuthentication(authCode: String) {
        mActivity.lifecycleScope.launch {
            try {
                val response =
                    mStudentLife.getAccessToken(
                        authCode,
                        "authorization_code",
                        clientID,
                        redirectUri,
                        codeVerifier,
                    )

                if (response.isSuccessful) {
                    val t: AccessTokenResponse? = response.body()
                    FirebaseAnalytics.getInstance(mActivity).logEvent("LoginEvent", null)

                    val accessToken = t?.accessToken
                    val editor = sp.edit()
                    editor.putString(getString(R.string.access_token), accessToken)
                    editor.putString(getString(R.string.refresh_token), t?.refreshToken)
                    editor.putString(getString(R.string.expires_in), t?.expiresIn)

                    val expiresInInt = t?.expiresIn!!.toInt() * 1000
                    Log.i("LoginWebview", "Expires In: $expiresInInt")
                    val currentTime = Calendar.getInstance().timeInMillis
                    editor.putLong(getString(R.string.token_expires_at), currentTime + expiresInInt)
                    editor.apply()
                    getUser(accessToken)
                } else {
                    val error = response.errorBody()
                    val exception = Exception(error?.string() ?: "Unknown Error")

                    FirebaseCrashlytics.getInstance().recordException(exception)
                    Log.e("Accounts", "Error fetching access token", exception)
                    Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                    mActivity.startLoginFragment()
                }
            } catch (e: Exception) {
                e.printStackTrace()

                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e("Accounts", "Error fetching access token", e)
                Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                mActivity.startLoginFragment()
            }
        }
    }

    private fun getUser(accessToken: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                mPlatform?.let {
                    val response =
                        it.getUser(
                            "Bearer $accessToken",
                            accessToken,
                        )

                    if (response.isSuccessful) {
                        val t = response.body()
                        val user = t?.user
                        val editor = sp.edit()
                        editor.putString(getString(R.string.first_name), user?.firstName)
                        editor.putString(getString(R.string.last_name), user?.lastName)
                        var initials = ""
                        try {
                            initials = initials + user?.firstName?.first() + user?.lastName?.first()
                            initials.capitalize()
                        } catch (ignored: Exception) {
                        }
                        editor.putString(getString(R.string.initials), initials)
                        editor.putString(getString(R.string.email_address), user?.email)
                        editor.putString(getString(R.string.pennkey), user?.username)
                        editor.apply()
                        mActivity.startHomeFragment()
                    } else {
                        val error = Exception(response.errorBody()?.string() ?: "Unknown Error")
                        Log.e("Accounts", "Error getting user $error")
                        Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                        mActivity.startLoginFragment()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
}
