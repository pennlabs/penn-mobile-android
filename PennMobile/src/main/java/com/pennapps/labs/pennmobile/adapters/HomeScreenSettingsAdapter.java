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
import com.pennapps.labs.pennmobile.classes.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2018-03-06.
 */

public class HomeScreenSettingsAdapter extends RecyclerView.Adapter<HomeScreenSettingsAdapter.CustomViewHolder> implements ItemTouchHelperAdapter {

    // all the categories
    private List<HomeScreenItem> mCategories;
    private Context mContext;
    private SharedPreferences sharedPref;
    private Set<CustomViewHolder> viewHolders = new HashSet<>();

    // categories must be already sorted by shared preferences
    public HomeScreenSettingsAdapter(Context context, List<HomeScreenItem> categories) {
        this.mCategories = new ArrayList<>();
        this.mContext = context;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        // set up shared preference data for new categories
        for (int index = 0; index < categories.size(); index++) {
            HomeScreenItem homeScreenItem = categories.get(index);
            String itemPrefName = mContext.getString(R.string.home_screen_pref) + "_" + homeScreenItem.getName();
            int itemLocation = sharedPref.getInt(itemPrefName, -1);
            if (itemLocation == -1) {
                // new HomeScreenItem
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(itemPrefName, index);
                editor.apply();
            }
            mCategories.add(homeScreenItem);
        }
    }

    // moving item up or down in list
    public boolean onItemMove(int fromPosition, int toPosition) {
        // moving down
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mCategories, i, i + 1);
            }
        }
        // moving up
        else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mCategories, i, i - 1);
            }
        }

        // update positions as shared preferences
        for (int i = 0; i < mCategories.size(); i++) {
            SharedPreferences.Editor editor = sharedPref.edit();
            HomeScreenItem card = mCategories.get(i);
            String cardPrefName = mContext.getString(R.string.home_screen_pref) + "_" + card.getName();
            int currentPos = sharedPref.getInt(cardPrefName, -1);

            int newPos = i;
            if (currentPos >= 100) {
                newPos += 100;
            }

            editor.putInt(cardPrefName, newPos);
            editor.apply();
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
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
        final String categoryName = mContext.getString(R.string.home_screen_pref) + "_" + category.getName();
        final int pos = sharedPref.getInt(categoryName, -1);
        holder.titleTextView.setText(category.getName());

        // which categories are shown on the home screen - update switch indicators
        if (pos < 100 || pos == -1) {
            categorySwitch.setChecked(false);
        } else {
            categorySwitch.setChecked(true);
        }

        categorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // checked - add category
                if (b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int currPos = sharedPref.getInt(categoryName, -1);
                    int newPos = currPos + 100;
                    editor.putInt(categoryName, newPos);
                    editor.apply();
                }
                // unchecked - remove category
                else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int currPos = sharedPref.getInt(categoryName, -1);
                    int newPos = currPos - 100;
                    editor.putInt(categoryName, newPos);
                    editor.apply();
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

        public CustomViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
        }
    }
}
