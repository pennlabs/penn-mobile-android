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
import java.util.*
import android.webkit.ValueCallback
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View.INVISIBLE
import android.widget.LinearLayout
import com.pennapps.labs.pennmobile.api.Platform
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

import com.pennapps.labs.pennmobile.api.Platform.*
import com.pennapps.labs.pennmobile.classes.*
import kotlinx.android.synthetic.main.fragment_login_webview.view.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.spec.IvParameterSpec

class LoginWebviewFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var headerLayout: LinearLayout
    lateinit var cancelButton: Button
    lateinit var user: Account
    private lateinit var mLabs: Labs
    private lateinit var mPlatform: Platform
    private lateinit var mActivity: MainActivity
    lateinit var sp: SharedPreferences
    lateinit var codeChallenge: String
    lateinit var platformAuthUrl: String
    lateinit var clientID: String
    lateinit var redirectUri: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_webview, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        mPlatform = MainActivity.getPlatformInstance()
        arguments?.let {
            user = arguments?.getSerializable("user") as Account
        }
        mActivity = activity as MainActivity
        sp = PreferenceManager.getDefaultSharedPreferences(mActivity)

        clientID = getString(R.string.clientID)
        redirectUri = getString(R.string.redirectUri)
        codeChallenge = getCodeChallenge(codeVerifier)
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
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(MyWebViewClient())

        cancelButton.setOnClickListener {
            mActivity.startLoginFragment()
        }

    }

    private fun encryptPassword(password: String) {
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
            var cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            cipher.init(Cipher.DECRYPT_MODE, secretkey, IvParameterSpec(encryptionIv))

            var passwordBytes = cipher.doFinal(encryptedPassword)
            var password = String(passwordBytes, Charset.forName("UTF-8"))
            return password
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
                initiateAuthentication(authCode)
            }
            if (url.contains("weblogin") && url.contains("pennkey")) {
                if (Build.VERSION.SDK_INT >= 19) {
                    webView.evaluateJavascript("document.getElementById('pennname').value;", ValueCallback<String> { s ->
                        if (s != null || s != "null") {
                           saveUsername(s)
                        }
                    })
                    webView.evaluateJavascript("document.getElementById('password').value;", ValueCallback<String> { s ->
                        if (s != null || s != "null") {
                            encryptPassword(s)
                        }
                    })
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

    private fun initiateAuthentication(authCode: String) {
        mPlatform.getAccessToken(authCode,
                "authorization_code", clientID, redirectUri, codeVerifier,
                object : Callback<AccessTokenResponse> {

                    override fun success(t: AccessTokenResponse?, response: Response?) {
                        if (response?.status == 200) {
                            val accessToken = t?.accessToken
                            val editor = sp.edit()
                            editor.putString(getString(R.string.access_token), accessToken)
                            editor.putString(getString(R.string.refresh_token), t?.refreshToken)
                            editor.putString(getString(R.string.expires_in), t?.expiresIn)
                            editor.apply()
                            getUser(accessToken)
                        }
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error fetching access token $error")
                    }
                })
    }

    private fun getUser(accessToken: String?) {
        mPlatform.getUser("Bearer " + accessToken, accessToken,
                object : Callback<GetUserResponse> {

                    override fun success(t: GetUserResponse?, response: Response?) {
                        Log.d("Accounts", "user: " + t?.user?.username)
                        val user = t?.user
                        val editor = sp.edit()
                        editor.putString(getString(R.string.first_name), user?.firstName)
                        editor.putString(getString(R.string.last_name), user?.lastName)
                        editor.putString(getString(R.string.email_address), user?.email)
                        editor.putString(getString(R.string.pennkey), user?.username)
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
                Log.d("Accounts", "accountID: " + t?.accountID)
                val editor = sp.edit()
                editor.putString(getString(R.string.accountID), t?.accountID)
                editor.apply()
                // After saving the account, go to homepage
                mActivity.startHomeFragment()
            }

            override fun failure(error: RetrofitError) {
                Log.e("Accounts", "Error saving account $error")
            }
        })
    }

    private fun getCodeChallenge(codeVerifier: String) : String {

        val digest = MessageDigest.getInstance("SHA-256")
        digest.reset()
        val byteArr = digest.digest(codeVerifier.toByteArray())
        val codeChallenge = BigInteger(1, byteArr).toString(16)

        return codeChallenge
    }
}