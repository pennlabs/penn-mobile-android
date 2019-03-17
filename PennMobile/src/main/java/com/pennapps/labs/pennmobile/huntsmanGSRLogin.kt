package com.pennapps.labs.pennmobile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.book_gsr.*

/**
 * A simple [Fragment] subclass.
 *
 */
class huntsmanGSRLogin : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_huntsman_gsrlogin, container, false)

        loadWebpage()

        return v
    }

    private fun loadWebpage() {
        // Get the web view settings instance
        webViewGSR.loadUrl("https://apps.wharton.upenn.edu/gsr/")
    }

    companion object {

        fun newInstance(): huntsmanGSRLogin {
            return huntsmanGSRLogin()
        }
    }
}
