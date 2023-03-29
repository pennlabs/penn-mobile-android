package com.pennapps.labs.pennmobile

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.Platform
import com.pennapps.labs.pennmobile.api.Platform.platformBaseUrl
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.GetUserResponse
import com.pennapps.labs.pennmobile.classes.SaveAccountResponse
import kotlinx.android.synthetic.main.fragment_login_webview.view.*
import org.apache.commons.lang3.RandomStringUtils
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_webview, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mPlatform = MainActivity.platformInstance
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
        platformAuthUrl = platformBaseUrl + "/accounts/authorize/?response_type=code&client_id=" +
                clientID + "&redirect_uri=" + redirectUri + "&code_challenge_method=S256" +
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
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.webViewClient = MyWebViewClient()

        cancelButton.setOnClickListener {
            mActivity.startLoginFragment()
        }

    }

    private fun encryptPassword(password: String) {
        if (Build.VERSION.SDK_INT >= 26) {
            var secretKey = createSecretKey() as SecretKey
            var cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
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

    private fun getDecodedPassword(): String? {
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

    private fun saveUsername(username: String) {
        val editor = sp.edit()
        editor.putString(getString(R.string.penn_user), username)
        editor.apply()
    }

    private fun createSecretKey(): SecretKey? {
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
            super.onReceivedHttpError(view, request, errorResponse)
            view?.visibility = INVISIBLE
            headerLayout.visibility = INVISIBLE
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
        Log.i("Authcode", "$authCode")
        Log.i("grantType", "authorization_code")
        Log.i("clientID", "$clientID")
        Log.i("redirectURI", "$redirectUri")
        Log.i("codeVerifier", "$codeVerifier")
        mPlatform?.getAccessToken(authCode,
            "authorization_code", clientID, redirectUri, codeVerifier,
            object : Callback<AccessTokenResponse> {

                override fun success(t: AccessTokenResponse?, response: Response?) {
                    if (response?.status == 200) {
                        val accessToken = t?.accessToken
                        val editor = sp.edit()
                        editor.putString(getString(R.string.access_token), accessToken)
                        editor.putString(getString(R.string.refresh_token), t?.refreshToken)
                        editor.putString(getString(R.string.expires_in), t?.expiresIn)
                        val calendar = Calendar.getInstance()
                        calendar.time = Date()
                        val expiresInInt = t?.expiresIn!!.toInt()
                        val date = Date(System.currentTimeMillis().plus(expiresInInt)) //or simply new Date();
                        editor.putLong(getString(R.string.token_generated), date.time)
                        editor.apply()
                        getUser(accessToken)
                    }
                }

                override fun failure(error: RetrofitError) {
                    Log.e("Accounts", "Error fetching access token $error", error)
                    Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                    mActivity.startLoginFragment()
                }
            })
    }

    private fun getUser(accessToken: String?) {
        mPlatform?.getUser("Bearer $accessToken", accessToken,
                object : Callback<GetUserResponse> {

                    override fun success(t: GetUserResponse?, response: Response?) {
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
                        // saveAccount(Account(user?.firstName, user?.lastName,
                        //        user?.username, user?.pennid, user?.email, user?.affiliation), user?.username.toString(), accessToken)
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("Accounts", "Error getting user $error")
                        Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                        mActivity.startLoginFragment()
                    }
                })
    }

    private fun saveAccount(account: Account, pennkey: String, accessToken: String?) {
        mStudentLife.saveAccount("Bearer $accessToken", pennkey, account, object : Callback<SaveAccountResponse> {

            override fun success(t: SaveAccountResponse?, response: Response?) {
                val editor = sp.edit()
                editor.putString(getString(R.string.accountID), t?.accountID)
                editor.apply()
                // After saving the account, go to homepage
                mActivity.startHomeFragment()
            }

            override fun failure(error: RetrofitError) {
                Log.e("Accounts", "Error saving account $error", error)
                Toast.makeText(mActivity, "Error logging in", Toast.LENGTH_SHORT).show()
                mActivity.startLoginFragment()
            }
        })
    }

    private fun getCodeChallenge(codeVerifier: String): String {

        // Hash the code verifier
        val md = MessageDigest.getInstance("SHA-256")
        val byteArr = md.digest(codeVerifier.toByteArray())

        // Base-64 encode
        var codeChallenge = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(byteArr)
        } else {
            String(
                    android.util.Base64.encode(byteArr, android.util.Base64.DEFAULT),
                    Charsets.UTF_8)
        }

        // Replace characters to make it web safe
        codeChallenge = codeChallenge.replace("=", "")
        codeChallenge = codeChallenge.replace("+", "-")
        codeChallenge = codeChallenge.replace("/", "_")

        return codeChallenge
    }
}