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
import androidx.core.view.ViewCompat
import com.pennapps.labs.pennmobile.adapters.AboutAdapter

import kotlinx.android.synthetic.main.fragment_about.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.fragment_about.view.logo_gif_iv
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Handler
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.components.collapsingtoolbar.ToolbarBehavior
import com.pennapps.labs.pennmobile.utils.Utils
import kotlinx.android.synthetic.main.fragment_about.view.appbar_home
import kotlinx.android.synthetic.main.fragment_about.view.date_view
import kotlinx.android.synthetic.main.fragment_about.view.profile
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

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
        if (Build.VERSION.SDK_INT > 20 && gif is AnimatedVectorDrawable) {
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
        val members = arrayListOf("Marta García Ferreiro", "Davies Lumumba",
                "Sahit Penmatcha", "Varun Ramakrishnan", "Anna Wang", "Sophia Ye", "Awad Irfan",
                "Vishesh Patel", "Liz Powell", "Anna Jiang", "Rohan Chhaya")
        view.our_team_rv?.adapter = AboutAdapter(members)
        ViewCompat.setNestedScrollingEnabled(view.our_team_rv, false)

        view.learn_more_btn?.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://pennlabs.org"))
            startActivity(i)
        }

        view.feedback_btn?.setOnClickListener {
            val link = Intent(Intent.ACTION_VIEW, Uri.parse("https://airtable.com/shr1oylDR3qzCpTXq"))
            startActivity(link)
        }

        view.licenses_btn?.setOnClickListener {
            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M) {
                val webView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_licenses, null) as WebView
                webView.loadUrl("file:///android_asset/open_source_licenses.html")
                AlertDialog.Builder(mActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle(getString(R.string.action_licenses))
                        .setView(webView)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
            }
        }

        initAppBar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppBar(view)
    }

    override fun onResume() {
        super.onResume()
        val mActivity : MainActivity? = activity as MainActivity
        mActivity?.removeTabs()
        mActivity?.setTitle(R.string.about)
        val initials = sharedPreferences.getString(getString(R.string.initials), null)
        if (initials != null && initials.isNotEmpty()) {
            this.initials.text = initials
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.profile_background.setImageDrawable(
                        resources.getDrawable
                        (R.drawable.ic_guest_avatar, context?.theme))
            } else {
                @Suppress("DEPRECATION")
                this.profile_background.setImageDrawable(
                        resources.getDrawable
                        (R.drawable.ic_guest_avatar))
            }
        }
        if (Build.VERSION.SDK_INT > 17) {
            mActivity?.setSelectedTab(MainActivity.ABOUT)
        }
    }

    private fun initAppBar(view: View) {
        view.date_view.text = Utils.getCurrentSystemTime()
        // Appbar behavior init
        if (Build.VERSION.SDK_INT > 16) {
            (view.appbar_home.layoutParams
                    as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        }
        view.profile.setOnClickListener {
            //TODO: Account Settings
        }
    }

}
