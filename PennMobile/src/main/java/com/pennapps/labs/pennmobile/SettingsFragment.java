package com.pennapps.labs.pennmobile;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment to switch to appropriate settings fragment
 * Created by Adel on 5/30/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String category = getArguments().getString("category");
        if (category != null) {
            if (category.equals("pref_courses_settings")) {
                addPreferencesFromResource(R.xml.prefs_courses);
            } else if (category.equals("pref_dining_settings")) {
                addPreferencesFromResource(R.xml.prefs_dining);
            }
        }
    }
}