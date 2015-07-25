package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.MainActivity;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;

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

    public DiningAdapter(Context context, List<DiningHall> diningHalls) {
        super(context, R.layout.dining_list_item, diningHalls);
        inflater = LayoutInflater.from(context);
        mLabs = MainActivity.getLabsInstance();
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
        holder.hall = diningHall;

        holder.menuArrow.setVisibility(View.GONE);
        holder.openMeal.setVisibility(View.VISIBLE);
        holder.openClose.setVisibility(View.VISIBLE);

        holder.hallNameTV.setText(WordUtils.capitalizeFully(diningHall.getName()));

        if (diningHall.isResidential() && !diningHall.hasMenu()) {
            mLabs.daily_menu(diningHall.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<NewDiningHall>() {
                        @Override
                        public void call(NewDiningHall newDiningHall) {
                            diningHall.parseMeals(newDiningHall);
                            if (diningHall.hasMenu()) {
                                holder.menuArrow.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    });
        }

        if (diningHall.isOpen()) {
            holder.hallStatus.setText("Open");
            holder.hallStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.label_green));
            if (!diningHall.openMeal().equals("all")) {
                holder.openMeal.setText("Currently serving " + diningHall.openMeal());
            } else {
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            }
            holder.openClose.setText("Closes at " + diningHall.closingTime());
        } else {
            holder.hallStatus.setText("Closed");
            holder.hallStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.label_red));
            String meal = diningHall.nextMeal();
            if (meal.equals("") || meal.equals("all")) {
                view.findViewById(R.id.dining_hall_open_meal).setVisibility(View.GONE);
            } else {
                holder.openMeal.setText("Next serving " + meal);
            }
            String openingTime = diningHall.openingTime();
            if (openingTime.equals("")) {
                view.findViewById(R.id.dining_hall_open_close).setVisibility(View.GONE);
            } else {
                holder.openClose.setText("Opens at " + diningHall.openingTime());
            }
        }

        if (diningHall.hasMenu()) {
            holder.menuArrow.setVisibility(View.VISIBLE);
        }

        this.sort(new MenuComparator());
        return view;
    }

    private class MenuComparator implements Comparator<DiningHall> {
        @Override
        public int compare(DiningHall diningHall, DiningHall diningHall2) {
            if (diningHall.isResidential() && !diningHall2.isResidential()) {
                return -1;
            } else if (diningHall2.isResidential() && !diningHall.isResidential()) {
                return 1;
            } else {
                return 0;
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
