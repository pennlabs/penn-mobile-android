package com.pennapps.labs.pennmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.OnClick;

public class SettingsFragment extends PreferenceFragmentCompat {

    Preference accountSettings;
    Preference logoutButton;

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

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = sp.edit();

        accountSettings = findPreference("pref_account_edit");
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

        logoutButton = findPreference("pref_account_logout");
        logoutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure you want to logout?");
                dialog.setButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("logged_in", false);
                        editor.commit();
                        dialog.cancel();
                    }
                });
                dialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();

                return true;
            }
        });
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
