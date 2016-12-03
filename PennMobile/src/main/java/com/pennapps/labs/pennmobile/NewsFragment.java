package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsService;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;

public class NewsFragment extends ListFragment {

    private ListView mListView;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsIntent customTabsIntent;
    private Intent share;
    private CustomTabsSession session;
    private CustomTabsIntent.Builder builder;

    class CustomListAdapter extends ArrayAdapter<String> { //TODO figure out custom list adapter

        private final Activity context;
        private final String[] itemname;
        private NewsSite[] news;

        public CustomListAdapter(Activity context, String[] itemname, NewsSite[] news) {
            super(context, R.layout.fragment_news, itemname);
            // TODO Auto-generated constructor stub

            this.context=context;
            this.itemname=itemname;
        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.fragment_news, null,true);

            TextView newsName = (TextView) rowView.findViewById(R.id.news_name);
            ImageView newsLogo = (ImageView) rowView.findViewById(R.id.news_logo);
            TextView newsDetails = (TextView) rowView.findViewById(R.id.news_details);

            newsName.setText(news[position].getName());
//            newsLogo.setImageResource(imgid[position]);
            newsDetails.setText("Description "+itemname[position]);
            return rowView;

        };
    }

    class NewsSite {
        private String name, url, image;


        public NewsSite(String name, String url, String image) {
            this.name = name;
            this.url = url;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    class NewsCustomTabsServiceConnection extends CustomTabsServiceConnection {
        public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            mCustomTabsClient = client;
            mCustomTabsClient.warmup(0);
            session = mCustomTabsClient.newSession(null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCustomTabsClient = null;
            session = null;
            customTabsIntent = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
        builder = new CustomTabsIntent.Builder();
        share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        builder.setToolbarColor(0x3E50B4);
        builder.setStartAnimations(getContext(),
                android.support.design.R.anim.abc_popup_enter,
                android.support.design.R.anim.abc_popup_exit);
//                    builder.setExitAnimations(getContext(),
//                            android.support.design.R.anim.abc_popup_exit,
//                            android.support.design.R.anim.abc_popup_enter);
        CustomTabsServiceConnection connection = new NewsCustomTabsServiceConnection();
        final ArrayList<String> URLs = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        URLs.add("http://www.thedp.com/");
        URLs.add("http://www.34st.com/");
        URLs.add("http://www.thedp.com/blog/under-the-button/");
        titles.add("The Daily Pennsylvanian");
        titles.add("34th Street");
        titles.add("Under the Button");
        ArrayList<Bundle> urlList = new ArrayList<>();
        for (int i = 0; i < URLs.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(CustomTabsService.KEY_URL, new Parcelable() {
                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel parcel, int i) {
                    parcel.writeString(URLs.get(i));
                }
            });
            urlList.add(bundle);
        }
//        Log.d("Is savedInstance null?", Boolean.toString(savedInstanceState == null));
        // TODO fix null pointer exception for second argument in mayLaunchUrl
//        session.mayLaunchUrl(Uri.parse(URLs.get(0)), null, urlList);
        CustomTabsClient.bindCustomTabsService(getContext(),
                NewsCustomTabsServiceConnection.CUSTOM_TAB_PACKAGE_NAME, connection);
        addNews();
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
        ButterKnife.bind(this, v);
        return v;
    }

    private void addNews() {
        NewsSite dp = new NewsSite("The Daily Pennsylvanian", "http://www.thedp.com/", "thedp");
        NewsSite thirtyFour = new NewsSite("34th Street", "http://www.34st.com/", "thirtyfour");
        NewsSite utb = new NewsSite("Under the Button",
                "http://www.thedp.com/blog/under-the-button/", "utb");
        NewsSite[] allSites = {dp, thirtyFour, utb};
        ArrayAdapter<NewsSite> adapter = new ArrayAdapter<>(getContext(),
                R.layout.fragment_news, R.id.news_name, allSites);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String url = ((NewsSite) l.getItemAtPosition(position)).getUrl();
        if (url != null) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(browserIntent);
            share.putExtra(Intent.EXTRA_TEXT, url);
            builder.addMenuItem("Share", PendingIntent.getActivity(getContext(), 0,
                    share, PendingIntent.FLAG_CANCEL_CURRENT));
            customTabsIntent = builder.build();
            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
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
}
