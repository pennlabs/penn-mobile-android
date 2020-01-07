package com.pennapps.labs.pennmobile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.pennapps.labs.pennmobile.adapters.AboutAdapter

import kotlinx.android.synthetic.main.fragment_about.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener


class AboutFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_about, container, false)

        Glide.with(this).asGif().load(R.drawable.logo_gif_transparent).listener(object : RequestListener<GifDrawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: GifDrawable, model: Any, target: com.bumptech.glide.request.target.Target<GifDrawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                resource.setLoopCount(1)
                return false
            }
        }).into(v.logo_gif_iv)

        v.our_team_rv?.layoutManager = GridLayoutManager(context, 3)
        val members = arrayListOf("Marta García Ferreiro", "Davies Lumumba",
                "Sahit Penmatcha", "Varun Ramakrishnan", "Anna Wang", "Sophia Ye")
        v.our_team_rv?.adapter = AboutAdapter(members)

        v.learn_more_btn?.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://pennlabs.org"))
            startActivity(i)
        }

        v.licenses_btn?.setOnClickListener {
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
