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
    private int numCategories = 0;
    private Set<CustomViewHolder> viewHolders = new HashSet<>();

    public HomeScreenSettingsAdapter(Context context, List<HomeScreenItem> categories) {
        this.mCategories = new ArrayList<>();
        this.mContext = context;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        // determine order of cards
        for (int index = 0; index < categories.size(); index++) {
            // search all categories to find the one that belongs to correct index
            for (int j = 0; j < categories.size(); j++) {
                int position = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + j, -1);

                // first time
                if (position == -1) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(mContext.getString(R.string.home_screen_pref) + j, j);
                    editor.apply();
                    position = j;
                }

                // switch on
                if (position >= 100) {
                    position -= 100;
                }

                if (position == index) {
                    HomeScreenItem category = categories.get(j);
                    mCategories.add(index, category);
                    break;
                }
            }
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
            int currentPos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + card.getViewType(), -1);

            int newPos = i;
            if (currentPos >= 100) {
                newPos += 100;
            }

            editor.putInt(mContext.getString(R.string.home_screen_pref) + card.getViewType(), newPos);
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
        final int categoryID = category.getViewType();
        final int pos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, categoryID);
        holder.titleTextView.setText(category.getName());

        // which categories are shown on the home screen - update switch indicators
        if (pos < 100) {
            categorySwitch.setChecked(false);
        } else {
            categorySwitch.setChecked(true);
            numCategories++;
        }

        categorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // checked - add category
                if (b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int currPos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
                    int newPos = currPos + 100;
                    editor.putInt(mContext.getString(R.string.home_screen_pref) + categoryID, newPos);
                    editor.apply();
                    numCategories++;
                }
                // unchecked - remove category
                else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    int currPos = sharedPref.getInt(mContext.getString(R.string.home_screen_pref) + categoryID, -1);
                    int newPos = currPos - 100;
                    editor.putInt(mContext.getString(R.string.home_screen_pref) + categoryID, newPos);
                    editor.apply();
                    numCategories--;
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

    private void reset() {
        for (int i = 0; i < 6; i++) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(mContext.getString(R.string.home_screen_pref) + i, i);
            editor.apply();
        }
    }
}
