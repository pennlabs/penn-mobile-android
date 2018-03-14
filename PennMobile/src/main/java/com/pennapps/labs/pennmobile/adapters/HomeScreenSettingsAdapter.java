package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2018-03-06.
 */

public class HomeScreenSettingsAdapter extends RecyclerView.Adapter<HomeScreenSettingsAdapter.CustomViewHolder> {

    // all the categories
    private List<HomeScreenItem> mCategories;
    private Context mContext;
    private SharedPreferences sharedPref;
    private int numCategories = 0;
    private Set<CustomViewHolder> viewHolders = new HashSet<>();

    public HomeScreenSettingsAdapter(Context context, List<HomeScreenItem> categories) {
        this.mCategories = categories;
        this.mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.home_screen_settings_item, parent, false);
        return new CustomViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        viewHolders.add(holder);
        final HomeScreenItem category = mCategories.get(position);
        Switch categorySwitch = holder.aSwitch;
        final int categoryID = category.getViewType();
        final int pos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
        holder.titleTextView.setText(category.getName());

        // which categories are shown on the home screen - update switch indicators
        if (pos == -1) {
            categorySwitch.setChecked(false);
        } else {
            categorySwitch.setChecked(true);
            numCategories++;
            holder.positionTextView.setText("position in home screen: " + pos);
        }

        categorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // checked - add category
                if (b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(mContext.getString(R.string.home_screen_pref) + categoryID, numCategories);
                    editor.apply();
                    numCategories++;
                }
                // unchecked - remove category
                else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int removedPos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
                    editor.putInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
                    editor.apply();
                    numCategories--;
                    // change the position of the cards after card removed
                    for (int i = 0; i < mCategories.size(); i++) {
                        HomeScreenItem cardAfter = mCategories.get(i);
                        int previousPos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + cardAfter.getViewType(), -1);
                        if (previousPos > removedPos) {
                            int newPos = previousPos - 1;
                            editor.putInt(mContext.getString(R.string.home_screen_pref) + cardAfter.getViewType(), newPos);
                            editor.apply();
                        }
                    }
                }

                // update position textviews
                for (CustomViewHolder viewHolder : viewHolders) {
                    int index = viewHolder.getAdapterPosition();
                    HomeScreenItem category = mCategories.get(index);
                    int categoryID = category.getViewType();
                    int position = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
                    if (position > -1) {
                        viewHolder.positionTextView.setText("position in home screen: " + position);
                    } else {
                        viewHolder.positionTextView.setText("");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        @Bind(R.id.home_screen_settings_switch)
        Switch aSwitch;
        @Bind(R.id.home_screen_category_name)
        TextView titleTextView;
        @Bind(R.id.home_screen_position)
        TextView positionTextView;

        public CustomViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
        }
    }
}
