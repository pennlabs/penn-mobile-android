package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.pennapps.labs.pennmobile.adapters.DiningAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class DiningFragment extends Fragment {

    @BindView(R.id.loadingPanel) RelativeLayout loadingPanel;
    @BindView(R.id.no_results) TextView no_results;
    @BindView(R.id.dining_halls_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.dining_swiperefresh) SwipeRefreshLayout swipeRefreshLayout;
    private Unbinder unbinder;

    private Labs mLabs;
    private MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        Fabric.with(getContext(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Dining")
                .putContentType("App Feature")
                .putContentId("1"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);
        unbinder = ButterKnife.bind(this, v);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiningHalls();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.color_accent, R.color.color_primary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(divider);
        getDiningHalls();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dining_sort, menu);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // sort the dining halls in the user-specified order
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

    private void getDiningHalls() {
        // Map each item in the list of venues to a Venue Observable, then map each Venue to a DiningHall Observable
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
                        DiningHall hall = createHall(venue);
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
                                    recyclerView.setAdapter(adapter);
                                    loadingPanel.setVisibility(View.GONE);
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
                                if (loadingPanel != null) {
                                    loadingPanel.setVisibility(View.GONE);
                                }
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                                try {
                                    swipeRefreshLayout.setRefreshing(false);
                                } catch (NullPointerException e){
                                    //it has gone to another page.
                                }
                            }
                        });
                    }
                });
    }

    // Takes a venue then adds an image and modifies venue name if name is too long
    private DiningHall createHall(Venue venue) {
        switch (venue.id) {
            case 593:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_commons);
            case 636:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_hill_house);
            case 637:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_kceh);
            case 638:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_hillel);
            case 639:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_houston);
            case 640:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_marks);
            case 641:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_accenture);
            case 642:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_joes_cafe);
            case 1442:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_nch);
            case 747:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_mcclelland);
            case 1057:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_gourmet_grocer);
            case 1058:
                return new DiningHall(venue.id, "Tortas Frontera", venue.isResidential(), venue.getHours(), venue, R.drawable.dining_tortas);
            case 1163:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_commons);
            case 1731:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_nch);
            case 1732:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_mba_cafe);
            case 1733:
                return new DiningHall(venue.id, "Pret a Manger Locust", venue.isResidential(), venue.getHours(), venue, R.drawable.dining_pret_a_manger);
            default:
                return new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours(), venue, R.drawable.dining_commons);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
        //mActivity.setNav(R.id.nav_dining);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
