package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.DiningFragment;
import com.pennapps.labs.pennmobile.DirectoryFragment;
import com.pennapps.labs.pennmobile.FlingFragment;
import com.pennapps.labs.pennmobile.GsrFragment;
import com.pennapps.labs.pennmobile.LaundryActivity;
import com.pennapps.labs.pennmobile.NewsFragment;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.RegistrarFragment;
import com.pennapps.labs.pennmobile.classes.HomeScreenCell;
import com.pennapps.labs.pennmobile.classes.HomeScreenItem;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jackie on 2018-03-04.
 */

public class HomeScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<HomeScreenItem> mCategories;
    private List<HomeScreenCell> mCells;

    // laundry
    private LaundryHomeAdapter laundryHomeAdapter;
    private List<LaundryRoom> mLaundryRooms;

    public HomeScreenAdapter(Context context, List<HomeScreenItem> categories, List<HomeScreenCell> cells, List<LaundryRoom> laundryRooms) {
        this.mContext = context;
        this.mCategories = categories;
        this.mCells = cells;
        this.mLaundryRooms = laundryRooms;
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
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_courses, parent, false);
                return new CoursesViewHolder(view, mContext);
            case 1:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_dining, parent, false);
                return new DiningViewHolder(view, mContext);
            case 2:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_gsr, parent, false);
                return new GSRViewHolder(view, mContext);
            case 3:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_laundry, parent, false);
                return new LaundryViewHolder(view, mContext);
            case 4:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_directory, parent, false);
                return new DirectoryViewHolder(view, mContext);
            case 5:
                view = LayoutInflater.from(mContext).inflate(R.layout.home_cardview_card_news, parent, false);
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

                /*
                // get index of dining cell from API
                for (HomeScreenCell cell : mCells) {
                    if (cell.getType().equals("dining")) {
                        List<Integer> diningVenues = cell.getInfo().getVenues();
                        String s1 = "";
                        for (Integer venue : diningVenues) {
                            s1 += venue.toString() + " ";
                        }
                        diningViewHolder.infoTextView.setText(s1);
                    }
                }*/
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

                // set up recycler view with the laundry rooms
                /*
                RecyclerView recyclerView = laundryViewHolder.laundryRecyclerView;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                recyclerView.setLayoutManager(linearLayoutManager);
                laundryHomeAdapter = new LaundryHomeAdapter(mLaundryRooms);
                recyclerView.setAdapter(laundryHomeAdapter); */
                break;
            case 4:
                DirectoryViewHolder directoryViewHolder = (DirectoryViewHolder) holder;
                String mapTitle = mCategories.get(position).getName();
                directoryViewHolder.titleTextView.setText(mapTitle);
                break;
            case 5:
                NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
                String newsTitle = mCategories.get(position).getName();
                newsViewHolder.titleTextView.setText(newsTitle);

                // news info
                /*
                for (HomeScreenCell cell : mCells) {
                    if (cell.getType().equals("news")) {
                        String title = cell.getInfo().getTitle();
                        newsViewHolder.newsTitle.setText(title);
                        String imageUrl = cell.getInfo().getImageUrl();
                        Picasso.with(mContext).load(imageUrl).into(newsViewHolder.newsImage);
                        String source = cell.getInfo().getSource();
                        newsViewHolder.newsSource.setText(source);
                        String date = cell.getInfo().getDate();
                        newsViewHolder.newsDate.setText(date);
                    }
                }
                */
                break;
            case 6:
                FlingViewHolder flingViewHolder = (FlingViewHolder) holder;
                String flingTitle = mCategories.get(position).getName();
                flingViewHolder.titleTextView.setText(flingTitle);
                break;
        }
    }

    // custom view holders for each category/cardview
    public class CoursesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @BindView(R.id.home_screen_cardview_title) TextView titleTextView;

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
        @BindView(R.id.home_screen_cardview_title)
        TextView titleTextView;
        @BindView(R.id.home_screen_info)
        TextView infoTextView;

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
        @BindView(R.id.home_screen_cardview_title)
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
        @BindView(R.id.home_screen_cardview_title)
        TextView titleTextView;
        @BindView(R.id.home_screen_laundry_recyclerview)
        RecyclerView laundryRecyclerView;

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

    public class DirectoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @BindView(R.id.home_screen_cardview_title)
        TextView titleTextView;

        public DirectoryViewHolder(View view, Context context) {
            super(view);
            ButterKnife.bind(this, view);
            this.context = context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            fragmentTransact(new DirectoryFragment());
        }
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context context;
        @BindView(R.id.home_screen_cardview_title)
        TextView titleTextView;
        @BindView(R.id.home_screen_news_title)
        TextView newsTitle;
        @BindView(R.id.home_screen_news_source)
        TextView newsSource;
        @BindView(R.id.home_screen_news_date)
        TextView newsDate;
        @BindView(R.id.home_screen_news_image)
        ImageView newsImage;

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
        @BindView(R.id.home_screen_cardview_title)
        TextView titleTextView;

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
