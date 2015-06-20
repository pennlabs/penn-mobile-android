package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    class MyTabAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> URLs;
        ArrayList<String> titles;

        public MyTabAdapter(FragmentManager fm) {
            super(fm);
            URLs = new ArrayList<>();
            titles = new ArrayList<>();
            addTab("http://www.thedp.com/", "The DP");
            addTab("http://www.34st.com/", "34th Street");
            addTab("http://www.thedp.com/blog/under-the-button/", "UTB");
            addTab("http://eventsatpenn.com/", "Events");
        }

        private void addTab(String url, String title) {
            URLs.add(url);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = new NewsTab();
            Bundle args = new Bundle();
            args.putString("url", URLs.get(position));
            myFragment.setArguments(args);
            return myFragment;
        }

        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getCount() {
            return URLs.size();
        }
    }

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
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        MyTabAdapter pageAdapter = new MyTabAdapter(getActivity().getSupportFragmentManager());
        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        return v;
    }
}
