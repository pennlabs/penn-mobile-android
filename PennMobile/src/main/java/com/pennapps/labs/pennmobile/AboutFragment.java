package com.pennapps.labs.pennmobile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        TextView featureRequest = (TextView) v.findViewById(R.id.about_desc);
        featureRequest.setClickable(true);
        featureRequest.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "PennMobile was developed by Penn Labs, with funding<br>" +
                "and support from the Undergraduate Assembly. <br><br> &copy; 2014 Penn Labs <br><br>" +
                "<a href='mailto:pennappslabs@gmail.com'>Request a feature</a><br><br>" +
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
