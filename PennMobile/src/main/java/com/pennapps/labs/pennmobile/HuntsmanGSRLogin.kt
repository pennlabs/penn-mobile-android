package com.pennapps.labs.pennmobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        webViewGSR.webViewClient = WebViewClient()
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    companion object {

        fun newInstance(): HuntsmanGSRLogin {
            return HuntsmanGSRLogin()
        }
    }
}
