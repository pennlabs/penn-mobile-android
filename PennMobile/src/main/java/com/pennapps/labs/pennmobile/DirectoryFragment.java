package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class DirectoryFragment extends ListFragment {

    private Labs mLabs;
    private MainActivity mActivity;
    private ListView mListView;
    private Context mContext;
    private SearchView searchView;
    private TextView no_results;

    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(android.R.id.list) ListView list;
    @Bind(R.id.directory_instructions) TextView directory_instructions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mContext = mActivity.getApplicationContext();
        mLabs = MainActivity.getLabsInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_directory, container, false);
        ButterKnife.bind(this, v);
        no_results = (TextView) mActivity.findViewById(R.id.no_results);
        loadingPanel.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.directory_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.directory_search);
        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchMenuItem.expandActionView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);

        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                searchView.clearFocus();
                mListView.setAdapter(null);
                directory_instructions.setVisibility(View.GONE);
                no_results.setVisibility(View.GONE);
                loadingPanel.setVisibility(View.VISIBLE);
                processQuery(arg0);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
    }

    private void processQuery(String query) {
        mLabs.people(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(final List<Person> people) {
                        DirectoryAdapter mAdapter = new DirectoryAdapter(mContext, people);
                        loadingPanel.setVisibility(View.GONE);
                        if (people.isEmpty()) {
                            no_results.setVisibility(View.VISIBLE);
                        } else {
                            mListView.setAdapter(mAdapter);
                            list.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        loadingPanel.setVisibility(View.GONE);
                        no_results.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.directory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
