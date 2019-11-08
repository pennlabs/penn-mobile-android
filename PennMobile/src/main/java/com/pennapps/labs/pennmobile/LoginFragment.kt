package com.pennapps.labs.pennmobile


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
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.api.PennInTouchNetworkManager
import com.pennapps.labs.pennmobile.classes.User
import java.util.*
import com.pennapps.labs.pennmobile.api.DatabaseHelper
import java.io.IOException
import android.widget.Toast




/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var cancelButton: Button
    lateinit var user: User
    private lateinit var mLabs: Labs


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

         val dbHelper = DatabaseHelper(context);
         try {
             dbHelper.createDataBase();
                } catch (io: IOException) {
                    throw Error("Unable to create database");
                }
                try {
                    dbHelper.openDataBase();
                } catch (sqle: SQLException) {
                    throw sqle;
                }
         try {
                    dbHelper.createDataBase();
                } catch (ioe: IOException) {
                    throw Error("Unable to create database");
                }
                try {
                    dbHelper.openDataBase();
                } catch (sqle: SQLException) {
                    throw sqle;
                }

         Toast.makeText(context, "Successfully Imported", Toast.LENGTH_SHORT).show()
        //need the table name for the query (accountdb is not the correct one and not sure how to find it)
        /* val c = dbHelper.query("accountdb", null, null, null, null, null, null);
         if (c.moveToFirst()) {
             do {
                 Toast.makeText(context,
                             "id: " + c.getString(0) + "\n" +
                                  "first: " + c.getString(1) + "\n" +
                                  "last: " + c.getString(2) + "\n" +
                                  "pennkey:  " + c.getString(3),
                                Toast.LENGTH_LONG).show();
                    } while (c.moveToNext());
                }*/

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

                if (url == "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do") {
                    obtainJavascriptInfo()
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
        webView.loadUrl("https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do")
        val webSettings = webView.getSettings()
        webSettings.setJavaScriptEnabled(true)

        webView.addJavascriptInterface(JavaScriptInterface(), JAVASCRIPT_OBJ)

        cancelButton.setOnClickListener {
            val fragmentTx = activity!!.supportFragmentManager.beginTransaction()
            fragmentTx.remove(this).commit()
        }

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
                "window.androidObj.userToAndroid('document.getElementById('pennkey').value');" +
                "window.androidObj.userToAndroid('document.getElementById('password').value');});"
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
            }        }
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
