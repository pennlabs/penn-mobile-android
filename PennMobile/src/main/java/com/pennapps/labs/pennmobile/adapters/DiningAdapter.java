package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.MenuFragment;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class DiningAdapter extends RecyclerView.Adapter<DiningAdapter.DiningViewHolder> {
    private Labs mLabs;
    private boolean[] loaded;
    private String sortBy;
    private List<DiningHall> diningHalls;
    private Context context;

    public DiningAdapter(Context context, List<DiningHall> diningHalls) {
        mLabs = MainActivity.getLabsInstance();
        this.context = context;
        loaded = new boolean[diningHalls.size()];
        this.diningHalls = diningHalls;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sortBy = sp.getString("dining_sortBy", "RESIDENTIAL");
        Collections.sort(this.diningHalls, new MenuComparator());
    }

    @NonNull
    @Override
    public DiningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dining_list_item, parent, false);
        return new DiningViewHolder(view, null);
    }

    @Override
    public void onBindViewHolder(@NonNull final DiningViewHolder holder, int position) {
        if (position < diningHalls.size()) {
            final DiningHall diningHall = diningHalls.get(position);
            holder.hall = diningHall;

            holder.menuArrow.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);

            holder.hallNameTV.setText(diningHall.getName());
            Picasso.get().load(diningHall.getImage()).fit().centerCrop().into(holder.hallImage);

            if (diningHall.isOpen()) {
                holder.hallStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.label_green));
                if (!diningHall.openMeal().equals("all")) {
                    holder.hallStatus.setText(getOpenStatusLabel(diningHall.openMeal()));
                }
                holder.hallHours.setText(diningHall.openTimes().toLowerCase());
            } else {
                holder.hallStatus.setText(R.string.dining_hall_closed);
                holder.hallStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.label_red));
                String openTimes = diningHall.openTimes();
                if (openTimes.length() == 0) {
                    holder.hallHours.setText(R.string.dining_closed_tomorrow);
                } else {
                    holder.hallHours.setText(diningHall.openTimes().toLowerCase());
                }
            }
            final int pos = position;
            if (diningHall.isResidential() && !loaded[pos]) {
                holder.progressBar.setVisibility(View.VISIBLE);
                mLabs.daily_menu(diningHall.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<DiningHall>() {
                            @Override
                            public void call(DiningHall newDiningHall) {
                                diningHall.sortMeals(newDiningHall.menus);
                                holder.progressBar.setVisibility(View.INVISIBLE);
                                holder.menuArrow.setVisibility(View.VISIBLE);
                                loaded[pos] = true;
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                holder.progressBar.setVisibility(View.VISIBLE);
                                holder.menuArrow.setVisibility(View.GONE);
                            }
                        });
            }
            else {
                holder.progressBar.setVisibility(View.GONE);
                holder.menuArrow.setVisibility(View.VISIBLE);
            }
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = ((MainActivity) context);
                    mainActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
                    mainActivity.getActionBarToggle().syncState();
                    Fragment fragment = new MenuFragment();

                    Bundle args = new Bundle();
                    args.putParcelable("DiningHall", diningHall);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.dining_fragment, fragment, "DINING_INFO_FRAGMENT")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            });
        }
    }

    private int getOpenStatusLabel(String openMeal) {
        switch (openMeal) {
            case "Breakfast":
                return R.string.dining_hall_breakfast;
            case "Brunch":
                return R.string.dining_hall_brunch;
            case "Lunch":
                return R.string.dining_hall_lunch;
            case "Dinner":
                return R.string.dining_hall_dinner;
            case "Late Night":
                return R.string.dining_hall_late_night;
            default:
                return R.string.dining_hall_open;
        }
    }

    @Override
    public int getItemCount() {
        return diningHalls.size();
    }

    private class MenuComparator implements Comparator<DiningHall> {
        @Override
        public int compare(DiningHall diningHall, DiningHall diningHall2) {
            switch (sortBy) {
                case "OPEN":
                    if (diningHall.isOpen() && !diningHall2.isOpen()) {
                        return -1;
                    } else if (diningHall2.isOpen() && !diningHall.isOpen()) {
                        return 1;
                    }
                    if (diningHall.isResidential() && !diningHall2.isResidential()) {
                        return -1;
                    } else if (diningHall2.isResidential() && !diningHall.isResidential()) {
                        return 1;
                    }
                    else {
                        return diningHall.getName().compareTo(diningHall2.getName());
                    }
                case "RESIDENTIAL":
                    if (diningHall.isResidential() && !diningHall2.isResidential()) {
                        return -1;
                    } else if (diningHall2.isResidential() && !diningHall.isResidential()) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                default:
                    return diningHall.getName().compareTo(diningHall2.getName());
            }
        }
    }

    public static class DiningViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dining_list_item_layout) ConstraintLayout layout;
        @BindView(R.id.dining_hall_name) TextView hallNameTV;
        @BindView(R.id.dining_hall_status) TextView hallStatus;
        @BindView(R.id.dining_hall_image) ImageView hallImage;
        @BindView(R.id.dining_hall_hours) TextView hallHours;
        @BindView(R.id.dining_hall_menu_indicator) ImageView menuArrow;
        @BindView(R.id.dining_progress) ProgressBar progressBar;
        public DiningHall hall;

        public DiningViewHolder(View view, DiningHall hall) {
            super(view);
            this.hall = hall;
            ButterKnife.bind(this, view);
        }
    }
}
