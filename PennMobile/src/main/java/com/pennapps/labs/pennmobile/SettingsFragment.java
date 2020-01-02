package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragmentCompat {
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
        ((MainActivity) getActivity()).removeTabs();
        getActivity().setTitle(R.string.action_settings);
        if (Build.VERSION.SDK_INT > 17){
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setSelectedTab(11);
        }
    }
}
