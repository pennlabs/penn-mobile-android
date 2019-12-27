package com.pennapps.labs.pennmobile

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_about, container, false)
        about_hi_tv?.movementMethod = LinkMovementMethod.getInstance()

        licenses_btn?.setOnClickListener {
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
            view.loadUrl("file:///android_asset/open_source_licenses.html")
            if (activity != null) {
                AlertDialog.Builder(activity!!, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle(getString(R.string.action_licenses))
                        .setView(view)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
            }
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).removeTabs()
        activity?.setTitle(R.string.about)
        if (Build.VERSION.SDK_INT > 17) {
            (activity as MainActivity)?.setSelectedTab(12)
        }
    }

}
