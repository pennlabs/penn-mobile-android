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
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.classes.User
import java.util.*
import android.webkit.ValueCallback
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

import com.pennapps.labs.pennmobile.api.Platform.*
import retrofit.ResponseCallback
import retrofit.RetrofitError
import retrofit.client.Response
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.spec.IvParameterSpec

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

    fun saveCredentials() {

    }

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

                if (url == "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do") {
                    var sessionid = ""
                    val cookies = CookieManager.getInstance().getCookie(url).split(";")
                    for (cookie in cookies) {
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


            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        webView.loadUrl(platformAuthUrl)
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(MyWebViewClient())

        cancelButton.setOnClickListener {
            val fragmentTx = activity!!.supportFragmentManager.beginTransaction()
            fragmentTx.remove(this).commit()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun encryptPassword(password: String){
        if (Build.VERSION.SDK_INT >= 26) {
            var secretKey = createSecretKey() as SecretKey
            var cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            var encryptionIv = cipher.iv
            var passwordBytes = password.toByteArray(Charset.forName("UTF-8"))
            var encryptedPasswordBytes = cipher.doFinal(passwordBytes)
            var encryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes)

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

    private fun getDecodedPassword() : String? {
        if (Build.VERSION.SDK_INT >= 26) {
            var base64EncryptedPassword = sp.getString("penn_password", "null")
            var base64EncryptionIv = sp.getString("encryptionIv", "null")

            var encryptionIv = Base64.getDecoder().decode(base64EncryptionIv)
            var encryptedPassword = Base64.getDecoder().decode(base64EncryptedPassword)

            var keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            var secretkey = keyStore.getKey("Key", null) as SecretKey
            var cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            cipher.init(Cipher.DECRYPT_MODE, secretkey, IvParameterSpec(encryptionIv))

            var passwordBytes = cipher.doFinal(encryptedPassword)
            var password = String(passwordBytes, Charset.forName("UTF-8"))
            return password
        } else{
            return null
        }
    }

    private fun saveUsername(username: String){
        val editor = sp.edit()
        editor.putString("penn_user", username)
        editor.commit()
        editor.apply()
    }

    private fun createSecretKey (): SecretKey? {
        if (Build.VERSION.SDK_INT >= 23) {
            var keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(KeyGenParameterSpec.Builder("Key", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            return keyGenerator.generateKey()
        } else {
            return null
        }
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {

            if (url.contains("callback")) {
                val urlArr = url.split("?code=").toTypedArray()
                val authCode = urlArr[urlArr.size - 1]
                Log.d("Accounts", authCode)
                getUser(authCode)
            }
            if (url.contains("execution") && url!!.contains("s2")) {
                if (Build.VERSION.SDK_INT >= 19) {
                    webView.evaluateJavascript("document.getElementById('pennname').value;", ValueCallback<String> { s ->
                        if (s != null) {
                           saveUsername(s)
                        }
                    })
                    webView.evaluateJavascript("document.getElementById('password').value;", ValueCallback<String> { s ->
                        if (s != null) {
                            encryptPassword(s)
                        }
                    })
                }
            }
            return super.shouldOverrideUrlLoading(view, url)

        }
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