package com.pennapps.labs.pennmobile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.pennapps.labs.pennmobile.adapters.AboutAdapter
import kotlinx.android.synthetic.main.fragment_about.view.*

class AboutFragment : Fragment() {

    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val gif = view.logo_gif_iv?.drawable
        if (gif is AnimatedVectorDrawable) {
            gif.start()
        } else {
            Glide.with(this).asGif().load(R.drawable.logo_gif_transparent).listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: GifDrawable, model: Any, target: com.bumptech.glide.request.target.Target<GifDrawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    resource.setLoopCount(1)
                    return false
                }
            }).into(view.logo_gif_iv)
        }

        view.our_team_rv?.layoutManager = GridLayoutManager(context, 3)
        view.alumni_rv?.layoutManager = GridLayoutManager(context, 3)
        val members = arrayListOf("Rohan Chhaya", "Julius Snipes", "Aaron Mei", "Trini Feng", "Vedha Avali")
        val alumni = arrayListOf("Marta García Ferreiro", "Varun Ramakrishnan", "Sahit Penmatcha",
            "Anna Wang", "Sophia Ye", "Awad Irfan", "Liz Powell", "Davies Lumumba", "Anna Jiang", "Ali Krema")
        view.our_team_rv?.adapter = AboutAdapter(members)
        view.alumni_rv?.adapter = AboutAdapter(alumni)

        ViewCompat.setNestedScrollingEnabled(view.our_team_rv, false)

        view.learn_more_btn?.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://pennlabs.org"))
            startActivity(i)
        }

        view.licenses_btn?.setOnClickListener {
            val webView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_licenses, null) as WebView
            webView.loadUrl("file:///android_asset/open_source_licenses.html")
            AlertDialog.Builder(mActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle(getString(R.string.action_licenses))
                    .setView(webView)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        }

        //mActivity.toolbar.visibility = View.VISIBLE
        mActivity.hideBottomBar()
        mActivity.setTitle(R.string.contacts)

        return view
    }

    override fun onResume() {
        super.onResume()
        val mActivity : MainActivity = activity as MainActivity
        mActivity.removeTabs()
        mActivity.setTitle(R.string.about)
        mActivity.setSelectedTab(MainActivity.MORE)
    }

}
