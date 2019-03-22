package com.pennapps.labs.pennmobile


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_huntsman_gsrlogin.*


class HuntsmanGSRLogin : Fragment() {

    // gsr details
    private lateinit var gsrID: String
    private lateinit var gsrLocationCode: String
    private lateinit var startTime: String
    private lateinit var endTime: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_huntsman_gsrlogin, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("@@@@@", "opened huntsman gsr login")
        loadWebpage()
    }

    private fun loadWebpage() {
        // Get the web view settings instance
        webViewGSR.webViewClient = object : WebViewClient() {

            // Called every time a URL finishes loading, not just when the first URL finishes loading
            override fun onPageFinished(view : WebView, url : String) {
                Log.d("@@@@@@", "page finished loading")
                if (url == "https://apps.wharton.upenn.edu/gsr/") {
                    // val cookies = CookieManager.getInstance().getCookie(url)
                    var sessionid = "INVALID"
                    val cookies = CookieManager.getInstance().getCookie(url).split(";")
                    for (cookie in cookies){
                        if (cookie.take(11) == " sessionid=") {
                            sessionid = cookie.substring(11)
                            Log.d("@@@@", "Session ID: " + sessionid)
                            break
                        }
                    }

                    // set up shared preferences
                    activity?.let { activity ->
                        Log.d("@@@@ activity", "storing sessionid " + sessionid)
                        val sp = PreferenceManager.getDefaultSharedPreferences(activity)
                        val editor = sp.edit()
                        editor.putString(getString(R.string.huntsmanGSR_SessionID), sessionid)
                        editor.apply()
                    }
                    Log.d("@@@@@", "switching to bookGsrFragment")

                    val bookGsrFragment = BookGsrFragment.newInstance(gsrID, gsrLocationCode, startTime, endTime)
                    val fragmentManager = (context as MainActivity).supportFragmentManager
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, bookGsrFragment)
                            .addToBackStack("GSR Fragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                }
            }
        }
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    companion object {

        fun newInstance(gsrID: String, gsrLocationCode: String, startTime: String, endTime: String): BookGsrFragment {
            val fragment = BookGsrFragment()
            val args = Bundle()
            args.putString("gsrID", gsrID)
            args.putString("gsrLocationCode", gsrLocationCode)
            args.putString("startTime", startTime)
            args.putString("endTime", endTime)
            fragment.arguments = args
            return fragment
        }
    }
}
