package com.pennapps.labs.pennmobile.more

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.*
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.home_news_card.view.*

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = mContext as MainActivity
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val editor = sp.edit()


        listView.isVerticalScrollBarEnabled = false

        val editProfilePref: Preference? = findPreference("pref_account_edit")


        editProfilePref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = Dialog(mContext)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.setContentView(R.layout.account_settings_dialog)
            val blurredBackground = dialog.findViewById<BlurView>(R.id.blurView)
            mActivity.runOnUiThread {
                /** Sets up blurred background on account edit dialog */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    blurredBackground.setupWith(view as ViewGroup)
                            .setFrameClearDrawable(ColorDrawable(
                                    ContextCompat.getColor(mContext, R.color.avail_color_green)))
                            .setBlurAlgorithm(RenderScriptBlur(mContext))
                            .setBlurRadius(20f)
                            .setHasFixedTransformationMatrix(true)
                            .setOverlayColor(resources.getColor(R.color.dialogBlurColorOverlay))
                } else {
                    blurredBackground.setBackgroundColor(ColorUtils
                            .setAlphaComponent(ContextCompat.getColor(
                                    mContext, R.color.black), 225))
                }
            }
            val cancelButton = dialog.findViewById<Button>(R.id.cancel_button)
            val saveButton = dialog.findViewById<Button>(R.id.save_button)
            val firstName = dialog.findViewById<EditText>(R.id.first_name)
            val lastName = dialog.findViewById<EditText>(R.id.last_name)
            val email = dialog.findViewById<EditText>(R.id.email)
            firstName.setText(sp.getString(getString(R.string.first_name), null))
            lastName.setText(sp.getString(getString(R.string.last_name), null))
            email.setText(sp.getString(getString(R.string.email_address), null))
            cancelButton.setOnClickListener { dialog.cancel() }
            saveButton.setOnClickListener {
                editor.putString(getString(R.string.first_name), firstName.text.toString())
                editor.putString(getString(R.string.last_name), lastName.text.toString())
                editor.putString(getString(R.string.email_address), email.text.toString())
                editor.apply()
                dialog.cancel()
            }
            dialog.window?.attributes = lp
            dialog.show()
            true
        }

        val userLoginPref: Preference? = findPreference("pref_account_login_logout")

        val pennKey = sp.getString(getString(R.string.pennkey), null)
        if (pennKey != null) {
            userLoginPref?.title = "Log out"
        } else {
            userLoginPref?.title = "Log in"
        }
        userLoginPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (pennKey != null) {
                val dialog = AlertDialog.Builder(context).create()
                dialog.setTitle("Log out")
                dialog.setMessage("Are you sure you want to log out?")
                dialog.setButton("Logout") { dialog, _ ->
                    CookieManager.getInstance().removeAllCookie()
                    editor.remove(getString(R.string.penn_password))
                    editor.remove(getString(R.string.penn_user))
                    editor.remove(getString(R.string.first_name))
                    editor.remove(getString(R.string.last_name))
                    editor.remove(getString(R.string.email_address))
                    editor.remove(getString(R.string.pennkey))
                    editor.remove(getString(R.string.accountID))
                    editor.remove(getString(R.string.access_token))
                    editor.remove(getString(R.string.guest_mode))
                    editor.apply()
                    dialog.cancel()
                    mActivity.startLoginFragment()
                }
                dialog.setButton2("Cancel") { dialog, _ -> dialog.cancel() }
                dialog.show()
            } else {
                mActivity.startLoginFragment()
            }
            true
        }

        val newsFeaturePref: Preference? = findPreference("pref_news_feature")

        newsFeaturePref?.setOnPreferenceClickListener {
            mActivity.fragmentTransact(NewsFragment())
            return@setOnPreferenceClickListener true
        }

        val contactsFeaturePref: Preference? = findPreference("pref_contacts_feature")

        contactsFeaturePref?.setOnPreferenceClickListener {
            mActivity.fragmentTransact(SaveContactsFragment())
            return@setOnPreferenceClickListener true
        }

        val aboutFeaturePref: Preference? = findPreference("pref_about_feature")

        aboutFeaturePref?.setOnPreferenceClickListener {
            mActivity.fragmentTransact(AboutFragment())
            return@setOnPreferenceClickListener true
        }


        val pennLabsPref: Preference? = findPreference("pref_labs_link")

        pennLabsPref?.setOnPreferenceClickListener {
            openLink(PennLabs)
            return@setOnPreferenceClickListener true
        }

        val pennHomepagePref: Preference? = findPreference("pref_penn_link")

        pennHomepagePref?.setOnPreferenceClickListener {
            openLink(PennHomepage)
            return@setOnPreferenceClickListener true
        }
        val pennCampusExpressPref: Preference? = findPreference("pref_campus_express_link")

        pennCampusExpressPref?.setOnPreferenceClickListener {
            openLink(CampusExpress)
            return@setOnPreferenceClickListener true
        }
        val pennCanvasPref: Preference? = findPreference("pref_canvas_link")

        pennCanvasPref?.setOnPreferenceClickListener {
            openLink(Canvas)
            return@setOnPreferenceClickListener true
        }
        val pennInTouchPref: Preference? = findPreference("pref_penn_in_touch_link")

        pennInTouchPref?.setOnPreferenceClickListener {
            openLink(PennInTouch)
            return@setOnPreferenceClickListener true
        }
        val pennPortalPref: Preference? = findPreference("pref_penn_portal_link")

        pennPortalPref?.setOnPreferenceClickListener {
            openLink(PennPortal)
            return@setOnPreferenceClickListener true
        }

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }


    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    companion object {
        private const val PennLabs = "https://pennlabs.org"
        private const val PennHomepage = "https://www.upenn.edu"
        private const val  CampusExpress = "https://prod.campusexpress.upenn.edu"
        private const val  Canvas = "https://canvas.upenn.edu"
        private const val  PennInTouch= "https://pennintouch.apps.upenn.edu"
        private const val  PennPortal= "https://portal.apps.upenn.edu/penn_portal"
    }
}