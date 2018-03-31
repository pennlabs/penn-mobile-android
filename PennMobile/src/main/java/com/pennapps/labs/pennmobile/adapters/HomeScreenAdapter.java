package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.DiningFragment;
import com.pennapps.labs.pennmobile.FlingFragment;
import com.pennapps.labs.pennmobile.GsrFragment;
import com.pennapps.labs.pennmobile.LaundryActivity;
import com.pennapps.labs.pennmobile.MapFragment;
import com.pennapps.labs.pennmobile.NewsFragment;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.RegistrarFragment;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2018-03-04.
 */

public class HomeScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<HomeScreenItem> mCategories;

    public HomeScreenAdapter(Context context, List<HomeScreenItem> categories) {
        this.mContext = context;
        this.mCategories = categories;
    }

    @Override
    public int getItemViewType(int position) {
        HomeScreenItem category = mCategories.get(position);
        if (category == null) {
            return -1;
        } else {
            return category.getViewType();
        }
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_item, parent, false);
        // In order: Courses, Dining, GSR Booking, Laundry, Map, News
        switch (viewType) {
            case 0:
                return new CoursesViewHolder(view, mContext);
            case 1:
                return new DiningViewHolder(view, mContext);
            case 2:
                return new GSRViewHolder(view, mContext);
            case 3:
                return new LaundryViewHolder(view, mContext);
            case 4:
                return new MapViewHolder(view, mContext);
            case 5:
                return new NewsViewHolder(view, mContext);
            case 6:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_fling_card, parent, false);
                return new FlingViewHolder(view, mContext);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_empty_item, parent, false);
                return new EmptyViewHolder(view, mContext);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // In order: Courses, Dining, GSR Booking, Laundry, Map, News
        switch (holder.getItemViewType()) {
            case 0:
                CoursesViewHolder coursesViewHolder = (CoursesViewHolder) holder;
                String coursesTitle = mCategories.get(position).getName();
                coursesViewHolder.titleTextView.setText(coursesTitle);
                break;
            case 1:
                DiningViewHolder diningViewHolder = (DiningViewHolder) holder;
                String diningTitle = mCategories.get(position).getName();
                diningViewHolder.titleTextView.setText(diningTitle);
                break;
            case 2:
                GSRViewHolder gsrViewHolder = (GSRViewHolder) holder;
                String gsrTitle = mCategories.get(position).getName();
                gsrViewHolder.titleTextView.setText(gsrTitle);
                break;
            case 3:
                LaundryViewHolder laundryViewHolder = (LaundryViewHolder) holder;
                String laundryTitle = mCategories.get(position).getName();
                laundryViewHolder.titleTextView.setText(laundryTitle);
                break;
            case 4:
                MapViewHolder mapViewHolder = (MapViewHolder) holder;
                String mapTitle = mCategories.get(position).getName();
                mapViewHolder.titleTextView.setText(mapTitle);
                break;
            case 5:
                NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
                String newsTitle = mCategories.get(position).getName();
                newsViewHolder.titleTextView.setText(newsTitle);
                break;
            case 6:
                FlingViewHolder flingViewHolder = (FlingViewHolder) holder;
                String flingTitle = mCategories.get(position).getName();
                flingViewHolder.titleTextView.setText(flingTitle);
                ArrayList<String> sampleData = new ArrayList<>();
                sampleData.add("Penn Labs");
                sampleData.add("Penn Course Review");
                sampleData.add("PennMobile");
                sampleData.add("Penn Course Alert");
                sampleData.add("PennBasics");
                sampleData.add("OHQ");
                flingViewHolder.homeFlingPerformer1.setText(sampleData.get(0));
                flingViewHolder.homeFlingPerformer2.setText(sampleData.get(1));
                flingViewHolder.homeFlingPerformer3.setText(sampleData.get(2));
                flingViewHolder.homeFlingPerformer4.setText(sampleData.get(3));
                flingViewHolder.homeFlingTime1.setText("2011-present");
                flingViewHolder.homeFlingTime2.setText("2011-present");
                flingViewHolder.homeFlingTime3.setText("2015-present");
                flingViewHolder.homeFlingTime4.setText("2017-present");
                break;
        }
    }

    // custom view holders for each category/cardview
    public class CoursesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public CoursesViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new RegistrarFragment());
        }
    }

    public class DiningViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public DiningViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new DiningFragment());
        }
    }

    public class GSRViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public GSRViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new GsrFragment());
        }
    }

    public class LaundryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public LaundryViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent laundryIntent = new Intent(mContext, LaundryActivity.class);
            mContext.startActivity(laundryIntent);
        }
    }

    public class MapViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public MapViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new MapFragment());
        }
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @Bind(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public NewsViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new NewsFragment());
        }
    }

    public class FlingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        @Bind(R.id.home_fling_title)
        TextView titleTextView;
        @Bind(R.id.homeview_fling_performer_1)
        TextView homeFlingPerformer1;
        @Bind(R.id.homeview_fling_performer_2)
        TextView homeFlingPerformer2;
        @Bind(R.id.homeview_fling_performer_3)
        TextView homeFlingPerformer3;
        @Bind(R.id.homeview_fling_performer_4)
        TextView homeFlingPerformer4;
        @Bind(R.id.homeview_fling_time_1)
        TextView homeFlingTime1;
        @Bind(R.id.homeview_fling_time_2)
        TextView homeFlingTime2;
        @Bind(R.id.homeview_fling_time_3)
        TextView homeFlingTime3;
        @Bind(R.id.homeview_fling_time_4)
        TextView homeFlingTime4;

        public FlingViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new FlingFragment());
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        Context context;

        public EmptyViewHolder(View view, Context context) {
            super(view);
            this.context = context;
        }
    }

    private void fragmentTransact(Fragment fragment) {
        if (fragment != null) {
            final Fragment frag = fragment;
            if (mContext instanceof FragmentActivity) {
                try {
                    FragmentActivity activity = (FragmentActivity) mContext;
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, frag)
                            .addToBackStack("Main Activity")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                } catch (IllegalStateException e) {
                    //ignore because the onSaveInstanceState etc states are called when activity is going to background etc
                }
            }
        }
    }
}
