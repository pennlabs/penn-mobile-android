package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutFragment extends Fragment {
    private AlertDialog mAlertDialog;
    private Unbinder unbinder;

    @BindView(R.id.about_desc) TextView featureRequest;
    @BindView(R.id.labs_icon) ImageView labsIcon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).closeKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, v);
        featureRequest.setMovementMethod(LinkMovementMethod.getInstance());
        Picasso.get().load(R.drawable.labs_logo).fit().centerCrop().into(labsIcon);
        String text = "Penn Mobile was developed by Penn Labs, with funding<br>" +
                "and support from the Undergraduate Assembly. Special thanks to Vishwa Patel. <br><br> &copy; 2018 Penn Labs <br><br>" +
                "<a href='mailto:contact@pennlabs.org?subject=[Penn Mobile Android]'>Request a feature</a><br><br>" +
                "<a href='http://pennlabs.org'>More information</a>";
        featureRequest.setText(Html.fromHtml(text));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about);
        //((MainActivity) getActivity()).setNav(R.id.nav_about);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.about_desc)
    public void onClick() {
    }

    @OnClick(R.id.licenses)
    public void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        mAlertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.action_licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
