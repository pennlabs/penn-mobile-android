package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.daimajia.swipe.SwipeLayout;
import com.pennapps.labs.pennmobile.R;

// import org.mcsoxford.rss.RSSItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 8/11/2016.
 */
public class NsoAdapter extends ArrayAdapter<String> { //ArrayAdapter<RSSItem>
    private final LayoutInflater inflater;
    private Context mContext;

    public NsoAdapter(Context context, List<String> list) { //List<RSSItem> list
        super(context, R.layout.nso_list_item, list);
        inflater = LayoutInflater.from(context);
        mContext = context;
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

        SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.nso_swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.nso_swipe_drawer));

        final String item = getItem(position); //RSSItem item

        holder.event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to be implemented

            }
        });

        holder.tvName.setText(item); //setText(getTitleName(item))
        holder.tvTime.setText(item);
//        try {
//            holder.tvTime.setText(getEventTime(item));
//        } catch (ParseException e) {
//            //ignore
//            Log.d("NSO", "parse error:", e);
//        }
        holder.tvDescription.setText(item); //setText(getDescription(item))

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        Set<String> starredContacts = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_nso_star), new HashSet<String>());
        holder.star.setChecked(starredContacts.contains(item)); //contains(item.getTitle())
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                Set<String> buffer = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_nso_star), new HashSet<String>());
                Set<String> starredContacts = new HashSet<>(buffer);
                SharedPreferences.Editor editedPreferences = sharedPref.edit();
                ToggleButton star = (ToggleButton) v;
                boolean starred = star.isChecked();
                String currentTitle = item; //item.getTitle()
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
    public static String getTitleName(String item) { //RSSItem item
        String title = item; //item.getTitle()
        title = title.substring(title.indexOf("\">")+2);
        title = title.substring(0, title.indexOf("</a>"));
        while (title.contains("&amp;")) {
            title = title.replace("&amp;", "&");
        }
        return title;
    }

    /**
     * returns a parsed version of the time of the event
     * @param item the item to be parsed
     * @return the string to be displayed as the time
     * @throws ParseException the exception thrown if couldn't parse correctly.
     */
    private String getEventTime(String item) throws ParseException{ //RSSItem item
        String time = item; //item.getTitle()
        time = time.substring(time.indexOf("event/") + 6);
        time = time.substring(0, time.indexOf("/"));
        String starttime = time.substring(0, 17);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US);
        cal.setTime(sdf.parse(starttime));
        SimpleDateFormat out = new SimpleDateFormat("EEE M/dd K:mm a", Locale.US);
        String answer = out.format(cal.getTime());
        if (time.length() < 18) {
            return answer;
        }
        answer += " – ";
        String endtime = time.substring(18);
        out = new SimpleDateFormat("K:mm a", Locale.US);
        cal.setTime(sdf.parse(endtime));
        answer += out.format(cal.getTime());
        return answer;
    }

    /**
     * returns a parsed version of the description
     * @param item the item to be parsed
     * @return the string to be displayed as description
     */
    private String getDescription(String item) { //RSSItem item
        String description = item; //item.getDescription()
        description = description.substring(3);
        while (description.contains("&amp;")) {
            description = description.replace("&amp;", "&");
        }
        return description;
    }

    static class ViewHolder {
        @Bind(R.id.tv_event_name)
        TextView tvName;
        @Bind(R.id.tv_event_time) TextView tvTime;
        @Bind(R.id.tv_event_description) TextView tvDescription;
        @Bind(R.id.star_event) ToggleButton star;
        @Bind(R.id.event_icon)
        ImageView event;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
