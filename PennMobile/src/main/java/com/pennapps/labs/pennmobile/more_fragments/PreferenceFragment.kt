package com.pennapps.labs.pennmobile.more_fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.*
import com.pennapps.labs.pennmobile.components.dialog.CustomAlertDialogue
import kotlinx.android.synthetic.main.include_main.*
import java.util.*

/**
 * Created by Davies Lumumba Spring 2021
 */
class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.isVerticalScrollBarEnabled = false
        mActivity = mContext as MainActivity
        mActivity.toolbar.visibility = View.GONE
        val sp = PreferenceManager.getDefaultSharedPreferences(mActivity)
        val editor = sp.edit()

        val editProfilePref: Preference? = findPreference("pref_account_edit")
        editProfilePref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
                    editor.remove(getString(R.string.initials))
                    editor.apply()
                    dialog.cancel()
                    mActivity.startLoginFragment()
                }
                //dialog.setButton(2,"Cancel") { dialog, _ -> dialog.cancel() }
                dialog.show()
            } else {
                mActivity.startLoginFragment()
            }
            true
        }

        val newsFeaturePref: Preference? = findPreference("pref_news_feature")
        newsFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, NewsFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val contactsFeaturePref: Preference? = findPreference("pref_contacts_feature")
        contactsFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SupportFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val aboutFeaturePref: Preference? = findPreference("pref_about_feature")
        aboutFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, AboutFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val fitnessFeaturePref: Preference? = findPreference("pref_fitness_feature")
        fitnessFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, PottruckFragment())
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
            return@setOnPreferenceClickListener true
        }

        val sublettingFeaturePref: Preference? = findPreference("pref_subletting_feature")
        sublettingFeaturePref?.setOnPreferenceClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, SublettingFragment())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            return@setOnPreferenceClickListener true
        }


        val pennLabsPref: Preference? = findPreference("pref_labs_link")
        pennLabsPref?.setOnPreferenceClickListener {
            openLink(PennLabs)
            return@setOnPreferenceClickListener true
        }

        val feedbackPref: Preference? = findPreference("pref_feedback_link")
        feedbackPref?.setOnPreferenceClickListener {
            openLink(Feedback)
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

        val pathAtPennPref: Preference? = findPreference("pref_path_at_penn_link")
        pathAtPennPref?.setOnPreferenceClickListener {
            openLink(PathAtPenn)
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

        val alert: CustomAlertDialogue.Builder = CustomAlertDialogue.Builder(activity)
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
                                "Profile details updated successfully.", null,
                                R.color.sneakerBlurColorOverlay)
                        val userLoginPref: Preference? = findPreference("pref_account_login_logout")
                        userLoginPref?.summary = "You are currently signed in as ${
                            sharedPreference.getString(getString(R.string.first_name), null)
                        }"
                        activity?.findViewById<TextView>(R.id.initials)?.text = initials
                    } else {
                        (activity?.window?.decorView as ViewGroup).showSneakerToast(
                                "Complete required fields to update your profile information.", { showEditProfileDialog() },
                                R.color.sneakerWarningOverlay)
                    }
                    dialog.dismiss()
                }
                .setNegativeText("Cancel")
                .setNegativeColor(stream.customalert.R.color.negative)
                .setOnNegativeClicked { _, dialog -> dialog.dismiss() }
                .setLineInputHint(hints)
                .setLineInputText(text)
                .setDecorView(activity?.window?.decorView)
                .build()
        alert.show()


    }

    companion object {
        private const val PennLabs = "https://pennlabs.org"
        private const val Feedback = "https://airtable.com/appFRa4NQvNMEbWsA/shrn4VbSQa8QDj8OG"
        private const val PennHomepage = "https://www.upenn.edu"
        private const val CampusExpress = "https://prod.campusexpress.upenn.edu"
        private const val Canvas = "https://canvas.upenn.edu"
        private const val PathAtPenn = "https://path.at.upenn.edu/"
        private const val PennPortal = "https://portal.apps.upenn.edu/penn_portal"
    }
}
