package com.pennapps.labs.pennmobile


import android.content.SharedPreferences
import android.database.SQLException
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.Toast
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.User
import java.util.*
import java.io.BufferedReader
import java.io.InputStreamReader
import android.webkit.ValueCallback




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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        sp = PreferenceManager.getDefaultSharedPreferences(activity)

        val rows = ArrayList<List<String>>()
        var inputStreamReader = InputStreamReader(context!!.assets.open("accountdb.csv"))
        var bufferedReader = BufferedReader(inputStreamReader)
        val csvSplitBy = ","

        var line = bufferedReader.readLine()

        while (line != null) {
            val row = line.split(csvSplitBy)
            rows.add(row);
            line = bufferedReader.readLine()
        }

        inputStreamReader = InputStreamReader(context!!.assets.open("school_major_account.csv"))
        bufferedReader = BufferedReader(inputStreamReader);

        var accountId =""

        //pennkey should be the pennkey saved in the shared preferences
        //right now, using 'sahitpen' to just parse the data correctly.
        for (row in rows) {
            if (row.get(3).equals("sahitpen")) {
                accountId = row.get(0);
            }
        }

        Log.d("AccountId", accountId)

        var majorCode = ""
        var majorName = ""
        var gradYear = ""

        line = bufferedReader.readLine()
        while (line != null) {
            val row = line.split(", ")
            Log.d("Row", row.toString());
            if (row.get(0).equals(accountId)) {
                majorCode = row.get(1);
                majorName = row.get(2);
                gradYear = row.get(3);
            }
            line = bufferedReader.readLine()
        }


        Log.d("MajorCode", majorCode)
        Log.d("MajorName", majorName)
        Log.d("GradYear", gradYear)

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        arguments?.let {
            user = arguments?.getSerializable("user") as User
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        cancelButton = view.findViewById(R.id.cancel_button)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)


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

            
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl(loginURL);
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
                Toast.makeText(context, "user_name_set", Toast.LENGTH_LONG)
            }
        }

        fun set_password(pass: String) {
            activity?.let { activity ->
                val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                val editor = sp.edit()
                editor.putString("password", pass)
                editor.apply()
            }        }
    }

    companion object {
        private val JAVASCRIPT_OBJ = "javascript_obj"
    }


}

 class MyWebViewClient : WebViewClient() {

     override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
         Log.d("URL OVERRIDING", url)



         //save pennkey and password here


         return super.shouldOverrideUrlLoading(view, url)
     }

}
