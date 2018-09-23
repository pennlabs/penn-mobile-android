package com.pennapps.labs.pennmobile;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsService;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class NewsFragment extends ListFragment {

    private ListView mListView;
    private CustomTabsClient mCustomTabsClient;
    private CustomTabsIntent customTabsIntent;
    private Intent share;
    private CustomTabsSession session;
    private CustomTabsIntent.Builder builder;
    private boolean isCustomTabsSupported;

    class CustomListAdapter extends ArrayAdapter<String> {

        private final Context context;
        private NewsSite[] news;

        public CustomListAdapter(Context context, String[] newsNames, NewsSite[] news) {
            super(context, R.layout.fragment_news, newsNames);
            this.news = news;
            this.context=context;
        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView=inflater.inflate(R.layout.news_list_item, null,true);

            TextView newsName = (TextView) rowView.findViewById(R.id.news_name);
            ImageView newsLogo = (ImageView) rowView.findViewById(R.id.news_logo);
            TextView newsDetails = (TextView) rowView.findViewById(R.id.news_details);

            newsName.setText(news[position].getName());
            newsLogo.setImageResource(news[position].getImage());
            newsDetails.setText(news[position].getDescription());
            return rowView;

        }
    }

    class NewsSite {
        private String name, url, description;
        private int image;


        public NewsSite(String name, String url, String description, int image) {
            this.name = name;
            this.url = url;
            this.image = image;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public int getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }

        public String getDescription() { return description; }

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
            session.mayLaunchUrl(Uri.parse(URLs.get(0)), null, urlList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCustomTabsClient = null;
            session = null;
            customTabsIntent = null;
        }
    }

    private static boolean isChromeCustomTabsSupported(final Context context) {
        String SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
        Intent serviceIntent = new Intent(SERVICE_ACTION);
        serviceIntent.setPackage("com.android.chrome");
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CustomTabsServiceConnection connection = new NewsCustomTabsServiceConnection();
        isCustomTabsSupported = isChromeCustomTabsSupported(getContext());
        setHasOptionsMenu(true);
        mListView = getListView();
        builder = new CustomTabsIntent.Builder();
        share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        builder.setToolbarColor(0x3E50B4);
        builder.setStartAnimations(getContext(),
                android.support.design.R.anim.abc_popup_enter,
                android.support.design.R.anim.abc_popup_exit);

        CustomTabsClient.bindCustomTabsService(getContext(),
                NewsCustomTabsServiceConnection.CUSTOM_TAB_PACKAGE_NAME, connection);
        addNews();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).closeKeyboard();
        setHasOptionsMenu(true);

        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("News")
                .putContentType("App Feature")
                .putContentId("5"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    private void addNews() {
        String dpDescription = "The Daily Pennsylvanian is the independent student newspaper of the University of Pennsylvania.";
        String thirtyFourDescription = "34th Street Magazine is the DP's arts and entertainment weekly magazine.";
        String utbDescription = "Under The Button is Penn's 24/7 news and entertainment blog, known for its signature humor, gossip and snarky features.";
        NewsSite dp = new NewsSite("The Daily Pennsylvanian", "http://www.thedp.com/",
                dpDescription, R.drawable.thedp);
        NewsSite thirtyFour = new NewsSite("34th Street", "http://www.34st.com/",
                thirtyFourDescription, R.drawable.thirtyfour);
        NewsSite utb = new NewsSite("Under the Button",
                "https://www.underthebutton.com/", utbDescription, R.drawable.utb);
        NewsSite[] allSites = {dp, thirtyFour, utb};
        String[] newsUrls = new String[allSites.length];
        for (int i = 0; i < newsUrls.length; i++) {
            newsUrls[i] = allSites[i].getUrl();
        }
        CustomListAdapter adapter = new CustomListAdapter(getContext(), newsUrls, allSites);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String url = (String) l.getItemAtPosition(position);
        if (url != null) {
            if (isCustomTabsSupported) {
                share.putExtra(Intent.EXTRA_TEXT, url);
                builder.addMenuItem("Share", PendingIntent.getActivity(getContext(), 0,
                        share, PendingIntent.FLAG_CANCEL_CURRENT));
                customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
            } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
            }
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
