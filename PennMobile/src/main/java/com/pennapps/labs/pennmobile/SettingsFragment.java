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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference accountSettings;
    private Preference logInOutButton;
    private MainActivity mActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.ReferenceTheme);
        mActivity = (MainActivity) getActivity();
        LayoutInflater localInflater = inflater.cloneInContext(context);
        return super.onCreateView(localInflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
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

                firstName.setText(sp.getString(getString(R.string.first_name), null));
                lastName.setText(sp.getString(getString(R.string.last_name), null));
                email.setText(sp.getString(getString(R.string.email_address), null));

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editor.putString(getString(R.string.first_name), firstName.getText().toString());
                        editor.putString(getString(R.string.last_name), lastName.getText().toString());
                        editor.putString(getString(R.string.email_address), email.getText().toString());

                        editor.commit();
                        dialog.cancel();
                    }
                });

                dialog.getWindow().setAttributes(lp);
                dialog.show();

                return true;
            }
        });

        logInOutButton = findPreference("pref_account_login_logout");

        final String pennkey = sp.getString(getString(R.string.pennkey), "");

        if (!pennkey.equals("")) {
            logInOutButton.setTitle("Log out");
        } else {
            logInOutButton.setTitle("Log in");
        }

        logInOutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (!pennkey.equals("")) {
                    final AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                    dialog.setTitle("Log out");
                    dialog.setMessage("Are you sure you want to log out?");
                    dialog.setButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.webkit.CookieManager.getInstance().removeAllCookie();
                            editor.remove(getString(R.string.penn_password));
                            editor.remove(getString(R.string.penn_user));
                            editor.remove(getString(R.string.first_name));
                            editor.remove(getString(R.string.last_name));
                            editor.remove(getString(R.string.email_address));
                            editor.remove(getString(R.string.pennkey));
                            editor.apply();
                            dialog.cancel();
                            mActivity.startLoginFragment();
                        }
                    });
                    dialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                } else {
                    mActivity.startLoginFragment();
                }

                return true;
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        mActivity.removeTabs();
        mActivity.setTitle(R.string.action_settings);
        if (Build.VERSION.SDK_INT > 17){
            mActivity.setSelectedTab(11);
        }
    }
}
