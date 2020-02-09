package com.pennapps.labs.pennmobile

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT > 28) {
            setTheme(R.style.DarkModeApi29)
        }

        val fragmentManager = supportFragmentManager
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
        }).into(background_iv)

        login_button.setOnClickListener {
            val fragment = LoginFragment()
            fragmentManager.beginTransaction()
                    .add(fragment, "Login Fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }

        guest_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
