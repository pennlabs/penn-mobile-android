package com.pennapps.labs.pennmobile.more.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R

class SettingsFragment : PreferenceFragmentCompat() {
    private var accountSettings: Preference? = null
    private var logInOutButton: Preference? = null
    private lateinit var mActivity: MainActivity
    private lateinit var mContext: Context

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        addPreferencesFromResource(R.xml.preference)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val context: Context = ContextThemeWrapper(activity, R.style.ReferenceTheme)
        mActivity = activity as MainActivity
        mContext = context
        val localInflater = inflater.cloneInContext(context)
        return super.onCreateView(localInflater, container, savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val editor = sp.edit()
        accountSettings = findPreference("pref_account_edit")
        accountSettings?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
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
        logInOutButton?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (pennKey != null) {
                    AlertDialog
                        .Builder(context)
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Logout") { dialog, _ ->
                            Log.d("SettingsFragment", "Logout button clicked in dialog.")
                            CookieManager.getInstance().removeAllCookie()
                            editor.apply {
                                remove(getString(R.string.penn_password))
                                remove(getString(R.string.penn_user))
                                remove(getString(R.string.first_name))
                                remove(getString(R.string.last_name))
                                remove(getString(R.string.email_address))
                                remove(getString(R.string.pennkey))
                                remove(getString(R.string.accountID))
                                remove(getString(R.string.access_token))
                                remove(getString(R.string.guest_mode))
                                remove(getString(R.string.campus_express_token))
                                remove(getString(R.string.campus_token_expires_in))
                            }
                            dialog.dismiss()
                            Log.d("SettingsFragment", "SharedPreferences cleared, navigating to Login.")
                            mActivity.startLoginFragment()
                        }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        .create()
                        .show()
                    Log.d("SettingsFragment", "Logout confirmation dialog displayed.")
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
        mActivity.setSelectedTab(MainActivity.MORE)
    }
}
