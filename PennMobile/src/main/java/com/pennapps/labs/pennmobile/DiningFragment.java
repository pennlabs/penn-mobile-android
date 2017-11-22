package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.DiningAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class DiningFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private MainActivity mActivity;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
    @Bind(R.id.dining_swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.dining_cache_warning) TextView cache_warning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();

        // this prevents the refresh from being called when the list is not
        // scrolled to the top, which allows the user to scroll up
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int topRowVerticalPosition = 0;
                if (mListView != null && mListView.getChildCount() != 0) {
                    topRowVerticalPosition = mListView.getChildAt(0).getTop();
                }
                swipeRefreshLayout.setEnabled(i == 0 && topRowVerticalPosition >= 0);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);
        ButterKnife.bind(this, v);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiningHalls();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);
        getDiningHalls();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining_sort, menu);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = sp.getString("dining_sortBy", "RESIDENTIAL");
        if (order.equals("RESIDENTIAL")) {
            menu.findItem(R.id.action_sort_residential).setChecked(true);
        }
        else if (order.equals("NAME")) {
            menu.findItem(R.id.action_sort_name).setChecked(true);
        }
        else {
            menu.findItem(R.id.action_sort_open).setChecked(true);
        }
        Fragment diningInfoFragment = getFragmentManager().findFragmentByTag("DINING_INFO_FRAGMENT");
        menu.setGroupVisible(R.id.action_sort_by, diningInfoFragment == null || !diningInfoFragment.isVisible());
    }

    private void setSortByMethod(String method) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("dining_sortBy", method);
        editor.apply();

        getDiningHalls();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.onBackPressed();
                return true;
            case R.id.action_sort_open:
                setSortByMethod("OPEN");
                item.setChecked(true);
                return true;
            case R.id.action_sort_residential:
                setSortByMethod("RESIDENTIAL");
                item.setChecked(true);
                return true;
            case R.id.action_sort_name:
                setSortByMethod("NAME");
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();

        Fragment fragment = new MenuFragment();

        Bundle args = new Bundle();
        args.putParcelable("DiningHall", ((DiningAdapter.ViewHolder) v.getTag()).hall);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.dining_fragment, fragment, "DINING_INFO_FRAGMENT")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void getDiningHalls() {
        mLabs.venues()
                .flatMap(new Func1<List<Venue>, Observable<Venue>>() {
                    @Override
                    public Observable<Venue> call(List<Venue> venues) {
                        return Observable.from(venues);
                    }
                })
                .flatMap(new Func1<Venue, Observable<DiningHall>>() {
                    @Override
                    public Observable<DiningHall> call(Venue venue) {
                        DiningHall hall = new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue);
                        return Observable.just(hall);
                    }
                })
                .toList()
                .subscribe(new Action1<List<DiningHall>>() {
                    @Override
                    public void call(final List<DiningHall> diningHalls) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingPanel != null) {
                                    DiningAdapter adapter = new DiningAdapter(mActivity, diningHalls);
                                    adapter.addMenuLoadListener(new DiningAdapter.MenuLoadListener() {
                                        @Override
                                        public void onLoad() {
                                            saveCache(diningHalls);
                                        }
                                    });
                                    mListView.setAdapter(adapter);
                                    loadingPanel.setVisibility(View.GONE);
                                    cache_warning.setVisibility(View.GONE);
                                    if (diningHalls.size() > 0) {
                                        no_results.setVisibility(View.GONE);
                                    }
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e){
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<DiningHall> cachedInfo = loadCache();
                                if (cachedInfo == null) {
                                    if (loadingPanel != null) {
                                        loadingPanel.setVisibility(View.GONE);
                                    }
                                    if (no_results != null) {
                                        mListView.setAdapter(null);
                                        no_results.setVisibility(View.VISIBLE);
                                    }
                                }
                                else {
                                    if (loadingPanel != null) {
                                        cache_warning.setVisibility(View.VISIBLE);
                                        DiningAdapter adapter = new DiningAdapter(mActivity, cachedInfo);
                                        mListView.setAdapter(adapter);
                                        loadingPanel.setVisibility(View.GONE);
                                        if (cachedInfo.size() > 0) {
                                            no_results.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e) {
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }

    private void saveCache(List<DiningHall> data) {
        try {
            String cacheFile = new File(getContext().getCacheDir().getPath(), "dining.ser").getPath();
            FileOutputStream outFile = new FileOutputStream(cacheFile);
            ObjectOutputStream out = new ObjectOutputStream(outFile);
            out.writeObject(data);
            out.close();
            outFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<DiningHall> loadCache() {
        try {
            String cacheFile = new File(getContext().getCacheDir().getPath(), "dining.ser").getPath();
            if (!new File(cacheFile).isFile()) {
                return null;
            }
            FileInputStream inFile = new FileInputStream(cacheFile);
            ObjectInputStream in = new ObjectInputStream(inFile);
            List<DiningHall> data = (List<DiningHall>) in.readObject();
            in.close();
            inFile.close();
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
        mActivity.setNav(R.id.nav_dining);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
