package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Func1;

public class RegistrarSearchFragment extends Fragment {

    public static final String COURSE_ID_EXTRA = "COURSE_ID";
    private Labs mLabs;
    public static Fragment mFragment;
    private Activity mActivity;
    private boolean hideKeyboard;
    private RegistrarAdapter mAdapter;
    private SearchView searchView;

    @InjectView(R.id.no_results) TextView no_results;
    @InjectView(R.id.registrar_instructions) TextView registrar_instructions;
    @InjectView(R.id.loadingPanel) RelativeLayout loadingPanel;
    @InjectView(R.id.registrar_fragment) FrameLayout registrar_fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        hideKeyboard = false;
        mLabs = ((MainActivity) getActivity()).getLabsInstance();
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_registrar_search, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.registrar_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);
        searchView = (SearchView) menu.findItem(R.id.registrar_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
        if (hideKeyboard) {
            searchView.clearFocus();
        } else {
            searchView.setQuery("", false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.registrar, menu);
        loadingPanel.setVisibility(View.GONE);
        MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String input) {
                searchView.clearFocus();
                hideKeyboard = true;
                no_results.setVisibility(View.GONE);
                registrar_instructions.setVisibility(View.GONE);
                loadingPanel.setVisibility(View.VISIBLE);
                registrar_fragment.removeAllViews();
                searchCourses(input);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private void searchCourses(String query) {
        mLabs.courses(query)
            .observeOn(AndroidSchedulers.mainThread()).onErrorReturn(new Func1<Throwable, List<Course>>() {
                @Override
                public List<Course> call(Throwable throwable) {
                    return null;
                }
            })
            .subscribe(new Action1<List<Course>>() {
                @Override
                public void call(List<Course> courses) {
                    if (courses == null || courses.size() == 0) {
                        loadingPanel.setVisibility(View.GONE);
                        no_results.setVisibility(View.VISIBLE);
                        registrar_fragment.setVisibility(View.GONE);
                    } else {
                        registrar_fragment.setVisibility(View.VISIBLE);
                        no_results.setVisibility(View.GONE);
                        RegistrarListFragment listFragment = new RegistrarListFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        mAdapter = new RegistrarAdapter(mActivity.getApplicationContext(),
                                R.layout.registrar_list_item, courses);
                        listFragment.setListAdapter(mAdapter);
                        transaction.replace(R.id.registrar_fragment, listFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registrar);
    }
}


