package com.pennapps.labs.pennmobile.more.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.components.dialog.CustomAlertDialogue
import com.pennapps.labs.pennmobile.gsr.fragments.PottruckFragment
import com.pennapps.labs.pennmobile.home.fragments.NewsFragment
import com.pennapps.labs.pennmobile.more.viewmodels.PreferencesViewModel
import com.pennapps.labs.pennmobile.showSneakerToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Davies Lumumba Spring 2021
 */
class PreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var toolbar: Toolbar
    private val preferencesViewModel: PreferencesViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        listView.isVerticalScrollBarEnabled = false
        mActivity = mContext as MainActivity
        toolbar = mActivity.findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val editor = sp.edit()

        val editProfilePref: Preference? = findPreference("pref_account_edit")
        editProfilePref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                showEditProfileDialog()
                true
            }

        val userLoginPref: Preference? = findPreference("pref_account_login_logout")
        val pennKey = sp.getString(getString(R.string.pennkey), null)
        if (pennKey != null) {
            userLoginPref?.title = "Log out"
            userLoginPref?.summary = "You are currently signed in as ${
                sp.getString(getString(R.string.first_name), null)
            }"
            editProfilePref?.isEnabled = true
        } else {
            userLoginPref?.title = "Log in"
            userLoginPref?.summary = "You are currently logged out."
            editProfilePref?.isEnabled = false
            editProfilePref?.parent?.removePreference(editProfilePref)
            editProfilePref?.shouldDisableView = true
        }
        userLoginPref?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                if (pennKey != null) {
                    AlertDialog
                        .Builder(context)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Logout") { dialog, _ ->
                            deleteNotifToken(sp)
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
                                remove(getString(R.string.initials))
                            }
                            dialog.dismiss()
                            mActivity.startLoginFragment()
                        }.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        .create()
                        .show()
                } else {
                    mActivity.startLoginFragment()
                }
                true
            }

        val newsFeaturePref: Preference? = findPreference("pref_news_feature")
        newsFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, NewsFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val contactsFeaturePref: Preference? = findPreference("pref_contacts_feature")
        contactsFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, SupportFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val aboutFeaturePref: Preference? = findPreference("pref_about_feature")
        aboutFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, AboutFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val fitnessFeaturePref: Preference? = findPreference("pref_fitness_feature")
        fitnessFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, PottruckFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val pennLabsPref: Preference? = findPreference("pref_labs_link")
        pennLabsPref?.setOnPreferenceClickListener {
            openLink(PENNLABS)
            return@setOnPreferenceClickListener true
        }

        val feedbackPref: Preference? = findPreference("pref_feedback_link")
        feedbackPref?.setOnPreferenceClickListener {
            openLink(FEEDBACK)
            return@setOnPreferenceClickListener true
        }

        val pennHomepagePref: Preference? = findPreference("pref_penn_link")
        pennHomepagePref?.setOnPreferenceClickListener {
            openLink(PENN_HOMEPAGE)
            return@setOnPreferenceClickListener true
        }

        val pennCampusExpressPref: Preference? = findPreference("pref_campus_express_link")
        pennCampusExpressPref?.setOnPreferenceClickListener {
            openLink(CAMPUS_EXPRESS)
            return@setOnPreferenceClickListener true
        }

        val pennCanvasPref: Preference? = findPreference("pref_canvas_link")
        pennCanvasPref?.setOnPreferenceClickListener {
            openLink(CANVAS)
            return@setOnPreferenceClickListener true
        }

        val pathAtPennPref: Preference? = findPreference("pref_path_at_penn_link")
        pathAtPennPref?.setOnPreferenceClickListener {
            openLink(PATH_AT_PENN)
            return@setOnPreferenceClickListener true
        }

        val pennPortalPref: Preference? = findPreference("pref_penn_portal_link")
        pennPortalPref?.setOnPreferenceClickListener {
            openLink(PENN_PORTAL)
            return@setOnPreferenceClickListener true
        }
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    private fun openLink(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    @SuppressLint("RestrictedApi")
    private fun showEditProfileDialog() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor = sharedPreference.edit()

        val hints = ArrayList<String>()
        hints.add(getString(R.string.first_name))
        hints.add(getString(R.string.last_name))
        hints.add(getString(R.string.email_address))

        val text = ArrayList<String?>()
        text.add(sharedPreference.getString(getString(R.string.first_name), null))
        text.add(sharedPreference.getString(getString(R.string.last_name), null))
        text.add(sharedPreference.getString(getString(R.string.email_address), null))

        val alert: CustomAlertDialogue.Builder =
            CustomAlertDialogue
                .Builder(activity)
                .setStyle(CustomAlertDialogue.Style.INPUT)
                .setTitle("Contact Info")
                .setMessage("This information is used when booking GSRs and when displaying your name on the app.")
                .setPositiveText("Submit")
                .setPositiveColor(stream.customalert.R.color.positive)
                .setOnInputClicked { _, dialog, inputList ->
                    val firstName = inputList[0].trim()
                    val lastName = inputList[1].trim()
                    val email = inputList[2].trim()
                    if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                        editor.putString(getString(R.string.first_name), firstName)
                        editor.putString(getString(R.string.last_name), lastName)
                        editor.putString(getString(R.string.email_address), email)
                        val initials = firstName.first().toString() + lastName.first()
                        editor.putString(getString(R.string.initials), initials.capitalize())
                        editor.apply()
                        (activity?.window?.decorView as ViewGroup).showSneakerToast(
                            "Profile details updated successfully.",
                            null,
                            R.color.sneakerBlurColorOverlay,
                        )
                        val userLoginPref: Preference? = findPreference("pref_account_login_logout")
                        userLoginPref?.summary = "You are currently signed in as ${
                            sharedPreference.getString(getString(R.string.first_name), null)
                        }"
                        activity?.findViewById<TextView>(R.id.initials)?.text = initials
                    } else {
                        (activity?.window?.decorView as ViewGroup).showSneakerToast(
                            "Complete required fields to update your profile information.",
                            { showEditProfileDialog() },
                            R.color.sneakerWarningOverlay,
                        )
                    }
                    dialog.dismiss()
                }.setNegativeText("Cancel")
                .setNegativeColor(stream.customalert.R.color.negative)
                .setOnNegativeClicked { _, dialog -> dialog.dismiss() }
                .setLineInputHint(hints)
                .setLineInputText(text)
                .setDecorView(activity?.window?.decorView)
                .build()
        alert.show()
    }

    private fun deleteNotifToken(sp: SharedPreferences) {
        val bearerToken = "Bearer " + sp.getString(getString(R.string.access_token), "").toString()
        val notifToken = sp.getString(getString(R.string.notification_token), "").toString()
        val mNotificationAPI = MainActivity.notificationAPIInstance
        Log.i("Notification Token", notifToken)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                preferencesViewModel.deleteTokenResponse(mNotificationAPI, bearerToken, notifToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val PENNLABS = "https://pennlabs.org"
        private const val FEEDBACK = "https://airtable.com/appFRa4NQvNMEbWsA/shrn4VbSQa8QDj8OG"
        private const val PENN_HOMEPAGE = "https://www.upenn.edu"
        private const val CAMPUS_EXPRESS = "https://prod.campusexpress.upenn.edu"
        private const val CANVAS = "https://canvas.upenn.edu"
        private const val PATH_AT_PENN = "https://path.at.upenn.edu/"
        private const val PENN_PORTAL = "https://portal.apps.upenn.edu/penn_portal"
    }
}
