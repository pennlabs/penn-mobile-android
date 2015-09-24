package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.internal.view.ContextThemeWrapper;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(getActivity(), R.style.ReferenceTheme);
        LayoutInflater localInflater = inflater.cloneInContext(context);
        return super.onCreateView(localInflater, container, savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_settings);
        ((MainActivity)getActivity()).setNav(R.id.nav_pref);
    }
}
