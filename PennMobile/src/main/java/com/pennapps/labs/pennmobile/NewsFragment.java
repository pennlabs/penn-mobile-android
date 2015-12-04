package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class NewsFragment extends Fragment {

    TabAdapter pageAdapter;
    ViewPager pager;

    class TabAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> URLs;
        ArrayList<String> titles;

        public TabAdapter(FragmentManager fm) {
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

        @Override
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
        ((MainActivity) getActivity()).closeKeyboard();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        pageAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        pager = (ViewPager) v.findViewById(R.id.pager);
        ((MainActivity) getActivity()).addTabs(pageAdapter, pager);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.news_browser:
                String url = getCurrentTab();
                if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.news);
        ((MainActivity) getActivity()).setNav(R.id.nav_news);
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).removeTabs();
        super.onDestroyView();
    }

    private String getCurrentTab() {
        try {
            ViewFlipper flipper = (ViewFlipper) pager.getChildAt(pager.getCurrentItem());
            WebView tab = (WebView) ((NestedScrollView) flipper.getChildAt(1)).getChildAt(0);
            return tab.getUrl();
        } catch (Exception e) {
            return null;
        }
    }
}
