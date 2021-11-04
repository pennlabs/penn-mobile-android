package com.pennapps.labs.pennmobile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.view.ContextThemeWrapper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    private var accountSettings: Preference? = null
    private var logInOutButton: Preference? = null
    private lateinit var mActivity: MainActivity
    private lateinit var mContext: Context

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context: Context = ContextThemeWrapper(activity, R.style.ReferenceTheme)
        mActivity = activity as MainActivity
        mContext = context
        val localInflater = inflater.cloneInContext(context)
        return super.onCreateView(localInflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val editor = sp.edit()
        accountSettings = findPreference("pref_account_edit")
        accountSettings?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = Dialog(mContext)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.setContentView(R.layout.account_settings_dialog)
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
        logInOutButton = findPreference("pref_account_login_logout")
        val pennKey = sp.getString(getString(R.string.pennkey), null)
        if (pennKey != null) {
            logInOutButton?.title = "Log out"
        } else {
            logInOutButton?.title = "Log in"
        }
        logInOutButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.action_settings)
        if (Build.VERSION.SDK_INT > 17) {
            mActivity.setSelectedTab(MainActivity.MORE)
        }
    }
}