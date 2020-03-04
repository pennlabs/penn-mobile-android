package com.pennapps.labs.pennmobile.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.browser.customtabs.CustomTabsIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pennapps.labs.pennmobile.R;

import org.mcsoxford.rss.RSSItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason on 8/11/2016.
 */
public class NsoAdapter extends ArrayAdapter<RSSItem> {
    private final LayoutInflater inflater;
    private Context mContext;
    private boolean isCustomTabsSupported;
    private Intent share;
    private CustomTabsIntent.Builder builder;

    public NsoAdapter(Context context, List<RSSItem> list) {
        super(context, R.layout.nso_list_item, list);
        inflater = LayoutInflater.from(context);
        mContext = context;
        isCustomTabsSupported = isChromeCustomTabsSupported(context);
        if (isCustomTabsSupported) {
            builder = new CustomTabsIntent.Builder();
            share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            builder.setToolbarColor(0x3E50B4);
            builder.setStartAnimations(getContext(),
                    R.anim.abc_popup_enter,
                    R.anim.abc_popup_exit);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.nso_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final RSSItem item = getItem(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url = item.getLink();
                if (isCustomTabsSupported) {
                    share.putExtra(Intent.EXTRA_TEXT, url);
                    builder.addMenuItem("Share", PendingIntent.getActivity(getContext(), 0,
                            share, PendingIntent.FLAG_CANCEL_CURRENT));
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(mContext, url);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        holder.tvName.setText(getTitleName(item));

        try {
            holder.tvTime.setText(getEventTime(item));
        } catch (ParseException e) {
            //ignore
        }
        holder.tvDescription.setText(getDescription(item));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        Set<String> starredContacts = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_nso_star), new HashSet<String>());
        holder.star.setChecked(starredContacts.contains(item.getTitle()));
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                Set<String> buffer = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_nso_star), new HashSet<String>());
                Set<String> starredContacts = new HashSet<>(buffer);
                SharedPreferences.Editor editedPreferences = sharedPref.edit();
                ToggleButton star = (ToggleButton) v;
                boolean starred = star.isChecked();
                String currentTitle = item.getTitle();
                if (starred) {
                    if (currentTitle != null) {
                        starredContacts.add(currentTitle);
                        editedPreferences.putString(currentTitle + mContext.getResources().getString(R.string.search_nso_star),
                                currentTitle);
                    }
                } else {
                    starredContacts.remove(currentTitle);
                    if(currentTitle != null) {
                        editedPreferences.remove(currentTitle + mContext.getResources().getString(R.string.search_nso_star));
                    }
                }
                editedPreferences.putStringSet(mContext.getResources().getString(R.string.search_nso_star), starredContacts);
                editedPreferences.apply();
            }
        });

        return view;
    }

    /**
     * returns the parsed version of the title
     * @param item the item to be parsed
     * @return the string to be displayed as the title
     */
    public static String getTitleName(RSSItem item) {
        String title = item.getTitle();
        title = title.substring(title.indexOf("\">")+2);
        title = title.substring(0, title.indexOf("</a>"));
        while (title.contains("&amp;")) {
            title = title.replace("&amp;", "&");
        }
        while (title.contains("&#039;")) {
            title = title.replace("&#039;", "'");
        }
        return title;
    }

    /**
     * returns a parsed version of the time of the event
     * @param item the item to be parsed
     * @return the string to be displayed as the time
     * @throws ParseException the exception thrown if couldn't parse correctly.
     */
    private String getEventTime(RSSItem item) throws ParseException {
        String time = item.getTitle();
        time = time.substring(time.indexOf("event/") + 6);
        time = time.substring(0, time.indexOf("/"));
        String starttime = time.substring(0, 17);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US);
        cal.setTime(sdf.parse(starttime));
        SimpleDateFormat out = new SimpleDateFormat("EEE M/dd h:mm a", Locale.US);
        String answer = out.format(cal.getTime());
        if (time.length() < 18) {
            return answer;
        }
        answer += " – ";
        String endtime = time.substring(18);
        out = new SimpleDateFormat("h:mm a", Locale.US);
        cal.setTime(sdf.parse(endtime));
        answer += out.format(cal.getTime());
        return answer;
    }

    /**
     * returns a parsed version of the description
     * @param item the item to be parsed
     * @return the string to be displayed as description
     */
    private String getDescription(RSSItem item) {
        String description = item.getDescription();
        if (description.length() >= 4) {
            description = description.substring(3);
        }
        while (description.contains("&amp;")) {
            description = description.replace("&amp;", "&");
        }
        while (description.contains("&#039;")) {
            description = description.replace("&#039;", "'");
        }
        return description;
    }

    private static boolean isChromeCustomTabsSupported(final Context context) {
        String SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
        Intent serviceIntent = new Intent(SERVICE_ACTION);
        serviceIntent.setPackage("com.android.chrome");
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return !(resolveInfos == null || resolveInfos.isEmpty());
    }

    static class ViewHolder {
        @BindView(R.id.tv_event_name)
        TextView tvName;
        @BindView(R.id.tv_event_time) TextView tvTime;
        @BindView(R.id.tv_event_description) TextView tvDescription;
        @BindView(R.id.star_event) ToggleButton star;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
