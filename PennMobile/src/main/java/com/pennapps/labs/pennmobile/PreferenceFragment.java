package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PreferenceFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setTitle(R.string.action_settings);
        ((MainActivity)getActivity()).setNav(R.id.nav_pref);
    }

}
