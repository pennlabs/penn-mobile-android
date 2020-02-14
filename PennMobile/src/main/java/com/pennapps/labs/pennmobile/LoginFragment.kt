package com.pennapps.labs.pennmobile

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        val fragmentManager = mActivity.supportFragmentManager
        var gif = R.drawable.login_background
        if (Build.VERSION.SDK_INT > 28 && (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            gif = R.drawable.login_background_dark
        }

        Glide.with(this).asGif().load(gif).listener(object : RequestListener<GifDrawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                return false
            }
            override fun onResourceReady(resource: GifDrawable, model: Any, target: com.bumptech.glide.request.target.Target<GifDrawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                resource.setLoopCount(1)
                return false
            }
        }).into(v.background_iv)

        v.login_button?.setOnClickListener {
            val fragment = LoginWebviewFragment()
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }

        v.guest_button?.setOnClickListener {
            val editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit()
            editor.remove(getString(R.string.penn_password))
            editor.remove(getString(R.string.penn_user))
            editor.remove(getString(R.string.first_name))
            editor.remove(getString(R.string.last_name))
            editor.remove(getString(R.string.email))
            editor.apply()
            mActivity.startHomeFragment()
        }

        return v
    }

}
