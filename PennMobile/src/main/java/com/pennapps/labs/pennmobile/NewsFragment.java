package com.pennapps.labs.pennmobile;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
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
        ((MainActivity) getActivity()).addTabs(pageAdapter, pager, true);
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
        String url;
        switch (item.getItemId()) {
            case R.id.news_browser:
                url = getCurrentTab();
                if (url != null) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(browserIntent);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, url);
                    builder.addMenuItem("Share", PendingIntent.getActivity(getContext(), 0,
                            share, PendingIntent.FLAG_CANCEL_CURRENT));
                    builder.setToolbarColor(0x3E50B4);
                    builder.setStartAnimations(getContext(),
                            android.support.design.R.anim.abc_popup_enter,
                            android.support.design.R.anim.abc_popup_exit);
//                    builder.setExitAnimations(getContext(),
//                            android.support.design.R.anim.abc_popup_exit,
//                            android.support.design.R.anim.abc_popup_enter);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                }
                return true;
            case R.id.news_share:
                url = getCurrentTab();
                if (url != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(share, "Share link!"));
                }
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
            return (String) pageAdapter.getItem(pager.getCurrentItem()).getArguments().get("url");
        } catch (Exception e) {
            return null;
        }
    }
}
