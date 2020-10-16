package com.pennapps.labs.pennmobile

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import java.util.*
import android.webkit.ValueCallback
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.View.INVISIBLE
import android.widget.LinearLayout
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.Platform
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

import com.pennapps.labs.pennmobile.api.Platform.Companion.codeVerifier
import com.pennapps.labs.pennmobile.api.Platform.Companion.platformBaseUrl
import com.pennapps.labs.pennmobile.classes.*
import kotlinx.android.synthetic.main.fragment_login_webview.view.*
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.spec.IvParameterSpec

class LoginWebviewFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var headerLayout: LinearLayout
    lateinit var cancelButton: Button
    lateinit var user: Account
    private lateinit var mLabs: Labs
    private var mPlatform: Platform? = null
    private lateinit var mActivity: MainActivity
    private lateinit var oAuth2NetworkManager: OAuth2NetworkManager
    lateinit var sp: SharedPreferences
    lateinit var codeChallenge: String
    lateinit var platformAuthUrl: String
    private lateinit var clientID: String
    private lateinit var redirectUri: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_webview, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.labsInstance
        mPlatform = MainActivity.platformInstance
        arguments?.let {
            user = arguments?.getSerializable("user") as Account
        }
        mActivity = activity as MainActivity
        sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        oAuth2NetworkManager = OAuth2NetworkManager(mActivity)

        clientID = getString(R.string.clientID)
        redirectUri = getString(R.string.redirectUri)
        codeChallenge = OAuth2NetworkManager.getCodeChallenge(codeVerifier)
        platformAuthUrl = platformBaseUrl + "/accounts/authorize/?response_type=code&client_id=" +
                clientID+ "&redirect_uri=" + redirectUri + "&code_challenge_method=S256" +
                "&code_challenge=" + codeChallenge + "&scope=read+introspection&state="
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        headerLayout = view.linear_layout
        cancelButton = view.findViewById(R.id.cancel_button)

        webView.loadUrl(platformAuthUrl)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true;
        webView.webViewClient = MyWebViewClient()

        cancelButton.setOnClickListener {
            mActivity.startLoginFragment()
        }

    }

    private fun encryptPassword(password: String) {
        if (Build.VERSION.SDK_INT >= 26) {
            val secretKey = createSecretKey() as SecretKey
            val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val encryptionIv = cipher.iv
            val passwordBytes = password.toByteArray(Charset.forName("UTF-8"))
            val encryptedPasswordBytes = cipher.doFinal(passwordBytes)
            val encryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes)

            //save the encrypted password
            val spEditor = sp.edit()
            spEditor.putString("penn_password", encryptedPassword)
            spEditor.apply()
            spEditor.commit()
            spEditor.putString("encryptionIv", Base64.getEncoder().encodeToString(encryptionIv))
            spEditor.apply()
            spEditor.commit()
        }
    }

    fun getDecodedPassword(mActivity : MainActivity) : String? {
        if (Build.VERSION.SDK_INT >= 26) {
            sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
            val base64EncryptedPassword = sp.getString("penn_password", null)
            val base64EncryptionIv = sp.getString("encryptionIv", null)

            val encryptionIv = Base64.getDecoder().decode(base64EncryptionIv)
            val encryptedPassword = Base64.getDecoder().decode(base64EncryptedPassword)

            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretkey = keyStore.getKey("Key", null) as SecretKey
            val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            cipher.init(Cipher.DECRYPT_MODE, secretkey, IvParameterSpec(encryptionIv))

            val passwordBytes = cipher.doFinal(encryptedPassword)
            return String(passwordBytes, Charset.forName("UTF-8"))
        } else {
            return null
        }
    }

    private fun saveUsername(username: String){
        val editor = sp.edit()
        editor.putString(getString(R.string.penn_user), username)
        editor.apply()
    }

    private fun createSecretKey (): SecretKey? {
        if (Build.VERSION.SDK_INT >= 23) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(KeyGenParameterSpec.Builder("Key", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            return keyGenerator.generateKey()
        }
        return null
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
            view?.visibility = INVISIBLE
            headerLayout.visibility = INVISIBLE
            super.onReceivedHttpError(view, request, errorResponse)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
            if (url.contains("callback") && url.contains("?code=")) {
                val urlArr = url.split("?code=").toTypedArray()
                val authCode = urlArr[urlArr.size - 1]
                oAuth2NetworkManager.initiateAuthentication(authCode)
            }
            if (url.contains("weblogin") && url.contains("pennkey")) {
                if (Build.VERSION.SDK_INT >= 19) {
                    webView.evaluateJavascript("document.getElementById('pennname').value;", ValueCallback<String> { s ->
                        if (s != null && s != "null") {
                           saveUsername(s)
                        }
                    })
                    webView.evaluateJavascript("document.getElementById('password').value;", ValueCallback<String> { s ->
                        if (s != null && s != "null") {
                            encryptPassword(s)
                        }
                    })
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

}