package com.pennapps.labs.pennmobile;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    
    private MyTabAdapter pageAdapter  = null;
    private ViewPager pager            = null;
    
    public EventsFragment() {
        super();
    }

    class MyTabAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        private List<String> titles;

        public MyTabAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<Fragment>();
            this.titles    = new ArrayList<String>();
        }

        public void addItem(String url, String title) {
            Fragment myFragment = new NewsTab();
            Bundle args = new Bundle();
            args.putString("url", url);
            myFragment.setArguments(args);
            this.fragments.add(myFragment);
            this.titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        public CharSequence getPageTitle(int position) {
            return this.titles.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
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

        pageAdapter = new MyTabAdapter(getActivity().getSupportFragmentManager());

        pageAdapter.addItem("http://www.thedp.com/", "The DP");
        pageAdapter.addItem("http://www.34st.com/", "34th Street");
        pageAdapter.addItem("http://www.thedp.com/blog/under-the-button/", "Under the Button");
        pageAdapter.addItem("http://eventsatpenn.com/", "Events");

        pager = (ViewPager) v.findViewById(R.id.pager);
        // This gives the number of Fragments loaded outside the view.
        // Here set to the number of Fragments minus one, i.e., all Fragments loaded.
        // This might not be a good idea if there are many Fragments.
        pager.setOffscreenPageLimit(pageAdapter.getCount() - 1);
        pager.setAdapter(pageAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        return v;
    }
}
