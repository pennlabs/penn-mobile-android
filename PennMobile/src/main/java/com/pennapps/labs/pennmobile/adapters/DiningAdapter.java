package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class DiningAdapter extends ArrayAdapter<DiningHall> {
    private final LayoutInflater inflater;
    private Labs mLabs;
    private boolean[] loaded;
    private String sortBy;

    public DiningAdapter(Context context, List<DiningHall> diningHalls) {
        super(context, R.layout.dining_list_item, diningHalls);
        inflater = LayoutInflater.from(context);
        mLabs = MainActivity.getLabsInstance();
        loaded = new boolean[diningHalls.size()];

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sortBy = sp.getString("dining_sortBy", "RESIDENTIAL");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final DiningHall diningHall = getItem(position);
        ViewHolder hallHolder;
        if (view != null) {
            hallHolder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.dining_list_item, parent, false);
            hallHolder = new ViewHolder(view, diningHall);
            view.setTag(hallHolder);
        }

        final ViewHolder holder = hallHolder;
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.dining_progress);
        holder.hall = diningHall;

        holder.menuArrow.setVisibility(View.GONE);
        holder.openMeal.setVisibility(View.VISIBLE);
        holder.openClose.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        holder.hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));



        if (diningHall.isOpen()) {
            holder.hallStatus.setText(R.string.dining_hall_open);
            holder.hallStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.label_green));
            if (!diningHall.openMeal().equals("all")) {
                if (diningHall.openMeal().equals("Breakfast/Lunch/Dinner")){
                    holder.openMeal.setText(String.format("Currently serving all meals"));
                }
                else {
                    holder.openMeal.setText(String.format("Currently serving %s", diningHall.openMeal()));
                }
            } else {
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            }
            holder.openClose.setText(String.format("Closes at %s", diningHall.closingTime()));
        } else {
            holder.hallStatus.setText(R.string.dining_hall_closed);
            holder.hallStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.label_red));
            String meal = diningHall.nextMeal();
            if (meal.equals("") || meal.equals("all")){
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            } else if (meal.equals("Breakfast/Lunch/Dinner")){
                holder.openMeal.setText(String.format("Next serving all meals"));
            } else {
                holder.openMeal.setText(String.format("Next serving %s", meal));
            }
            String openingTime = diningHall.openingTime();
            if (openingTime.equals("")) {
                view.findViewById(R.id.dining_hall_open_close).setVisibility(View.GONE);
            } else {
                holder.openClose.setText(String.format("Opens at %s", diningHall.openingTime()));
            }
        }
        final int pos = position;
        if (diningHall.isResidential() && !loaded[pos]) {
            progressBar.setVisibility(View.VISIBLE);
            mLabs.daily_menu(diningHall.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<DiningHall>() {
                        @Override
                        public void call(DiningHall newDiningHall) {
                            diningHall.sortMeals(newDiningHall.menus);
                            progressBar.setVisibility(View.INVISIBLE);
                            holder.menuArrow.setVisibility(View.VISIBLE);
                            loaded[pos] = true;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            progressBar.setVisibility(View.VISIBLE);
                            holder.menuArrow.setVisibility(View.GONE);
                        }
                    });
        }
        else {
            progressBar.setVisibility(View.GONE);
            holder.menuArrow.setVisibility(View.VISIBLE);
        }
        this.sort(new MenuComparator());
        return view;
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

    public static class ViewHolder {
        @Bind(R.id.dining_hall_name) TextView hallNameTV;
        @Bind(R.id.dining_hall_status) TextView hallStatus;
        @Bind(R.id.dining_hall_open_meal) TextView openMeal;
        @Bind(R.id.dining_hall_open_close) TextView openClose;
        @Bind(R.id.dining_hall_menu_indicator) ImageView menuArrow;
        public DiningHall hall;

        public ViewHolder(View view, DiningHall hall) {
            this.hall = hall;
            ButterKnife.bind(this, view);
        }
    }
}
