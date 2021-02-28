package com.pennapps.labs.pennmobile.more

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.pennapps.labs.pennmobile.R

class PreferenceFragment : PreferenceFragmentCompat() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        listView.isVerticalScrollBarEnabled = false


        val pennLabsPref: Preference? = findPreference("pref_labs_link")

        pennLabsPref?.setOnPreferenceClickListener {
            openLink(PennLabs)
            return@setOnPreferenceClickListener true
        }

        val pennHompagePref: Preference? = findPreference("pref_penn_link")

        pennHompagePref?.setOnPreferenceClickListener {
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