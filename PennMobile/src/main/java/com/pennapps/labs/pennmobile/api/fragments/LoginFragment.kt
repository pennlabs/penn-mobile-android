package com.pennapps.labs.pennmobile.api.fragments

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
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val v = binding.root

        val fragmentManager = mActivity.supportFragmentManager
        val gif = R.drawable.login_background

        Glide
            .with(this)
            .asGif()
            .load(gif)
            .listener(
                object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                        isFirstResource: Boolean,
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: GifDrawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        resource.setLoopCount(1)
                        return false
                    }
                },
            ).into(binding.backgroundIv)

        binding.loginButton.setOnClickListener {
            val fragment = LoginWebviewFragment()
            fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }

        binding.guestButton.setOnClickListener {
            val editor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
            editor.remove(getString(R.string.penn_password))
            editor.remove(getString(R.string.penn_user))
            editor.remove(getString(R.string.first_name))
            editor.remove(getString(R.string.last_name))
            editor.remove(getString(R.string.email_address))
            editor.remove(getString(R.string.pennkey))
            editor.remove(getString(R.string.access_token))
            editor.remove(getString(R.string.accountID))
            editor.putString(getString(R.string.access_token), "")
            editor.putString(getString(R.string.refresh_token), "")
            editor.putString(getString(R.string.expires_in), "")
            editor.putBoolean(getString(R.string.guest_mode), true)
            editor.apply()
            mActivity.startHomeFragment()
        }

        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mActivity.hideBottomBar()
    }
}
