package com.pennapps.labs.pennmobile;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.OnClick;

public class PreferenceFragment extends PreferenceFragmentCompat {

    Preference accountSettings;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountSettings = findPreference("pref_account");
        accountSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Dialog dialog = new Dialog(getContext());

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.setContentView(R.layout.account_settings_dialog);

                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                Button saveButton = dialog.findViewById(R.id.save_button);
                final EditText firstName = dialog.findViewById(R.id.first_name);
                final EditText lastName = dialog.findViewById(R.id.last_name);
                final EditText email = dialog.findViewById(R.id.email);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString("first_name", firstName.getText().toString());
                        editor.putString("last_name", lastName.getText().toString());
                        editor.putString("email", email.getText().toString());

                        editor.commit();
                        dialog.cancel();
                    }
                });

                dialog.getWindow().setAttributes(lp);
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setTitle(R.string.action_settings);
        ((MainActivity)getActivity()).setNav(R.id.nav_pref);
    }
}
