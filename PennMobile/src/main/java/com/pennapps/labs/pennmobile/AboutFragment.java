package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        TextView featureRequest = (TextView) v.findViewById(R.id.about_desc);
        featureRequest.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
              }
        });
        featureRequest.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "PennMobile was developed by Penn Labs, with funding<br>" +
                "and support from the Undergraduate Assembly. Special thanks to Vishwa Patel. <br><br> &copy; 2014 Penn Labs <br><br>" +
                "<a href='mailto:pennappslabs@gmail.com?subject=[Penn Mobile Android]'>Request a feature</a><br><br>" +
                "<a href='http://pennlabs.org'>More information</a>";
        featureRequest.setText(Html.fromHtml(text));
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
