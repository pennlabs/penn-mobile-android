package com.pennapps.labs.pennmobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_huntsman_gsrlogin.*

class HuntsmanGSRLogin : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_huntsman_gsrlogin, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadWebpage()
    }

    private fun loadWebpage() {
        // Get the web view settings instance
        webViewGSR.webViewClient = object : WebViewClient() {

            // Called every time a URL finishes loading, not just when the first URL finishes loading
            override fun onPageFinished(view : WebView, url : String) {
                if (url == "https://apps.wharton.upenn.edu/gsr/") {
                    Log.d("@@@@", "user is already logged in, get the cookie")
                }
                else {
                    Log.d("@@@@@", "redirected to login page")
                }
            }
        }
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    companion object {

        fun newInstance(): HuntsmanGSRLogin {
            return HuntsmanGSRLogin()
        }
    }
}
