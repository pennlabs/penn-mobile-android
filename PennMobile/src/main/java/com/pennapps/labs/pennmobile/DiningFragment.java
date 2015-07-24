package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.adapters.DiningAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class DiningFragment extends ListFragment {

    private Labs mLabs;
    private ListView mListView;
    private MainActivity mActivity;
    public static Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        mActivity = (MainActivity) getActivity();
        mFragment = this;

        mActivity.closeKeyboard();

        getDiningHalls();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dining, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DiningHall diningHall = ((DiningAdapter.ViewHolder) v.getTag()).hall;
        if (diningHall.hasMenu()) {
            Fragment fragment = new MenuFragment();

            Bundle args = new Bundle();
            args.putParcelable("DiningHall", ((DiningAdapter.ViewHolder) v.getTag()).hall);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.dining_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
            onResume();
        }
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
                        DiningHall hall = new DiningHall(venue.id, venue.name, venue.isResidential(), venue.getHours());
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
                                DiningAdapter adapter = new DiningAdapter(mActivity, diningHalls);
                                mListView.setAdapter(adapter);
                                mActivity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showErrorToast();
                    }
                });
    }

    private void showErrorToast() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, R.string.no_results, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dining);
    }
}
