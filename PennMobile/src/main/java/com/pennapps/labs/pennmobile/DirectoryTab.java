package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Jason on 2/1/2016.
 */
public class DirectoryTab extends SearchFavoriteTab {

    private DirectoryAdapter mAdapter;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite_tab, container, false);
        unbinder = ButterKnife.bind(this, v);
        mRecyclerView = v.findViewById(R.id.fragment_search_favorite_recycler_view);
        initList();
        return v;
    }

    @Override
    public void processQuery (String query) {
        super.processQuery(query);
        processDirectoryQuery(query);
    }

    private void processDirectoryQuery(String query) {
        if (query.isEmpty()) {
            return;
        }
        mLabs.people(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(final List<Person> people) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            if (people.isEmpty()) {
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.GONE);
                                }
                            } else if (mRecyclerView != null) {
                                mAdapter = new DirectoryAdapter(mActivity, people);
                                mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                no_results.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        noResults();
                    }
                });
    }

    @Override
    public void initList() {
        if (fav) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            Gson gson = new Gson();
            Set<String> starred = sp.getStringSet(getString(R.string.search_dir_star), new HashSet<String>());
            if (starred.isEmpty()) {
                notFavoriteInit();
            } else {
                if (loadingPanel.getVisibility() == View.VISIBLE) {
                    loadingPanel.setVisibility(View.GONE);
                }
                if (mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                if (no_results.getVisibility() == View.VISIBLE) {
                    no_results.setVisibility(View.GONE);
                }
                if (search_instructions.getVisibility() == View.VISIBLE) {
                    search_instructions.setVisibility(View.GONE);
                }
                List<Person> people = new LinkedList<>();
                for (String s : starred) {
                    String details = sp.getString(s + getString(R.string.search_dir_star), "");
                    if (!details.isEmpty()) {
                        Person person = gson.fromJson(details, Person.class);
                        people.add(person);
                    }
                }
                mAdapter = new DirectoryAdapter(mActivity, people);
                mRecyclerView.setAdapter(mAdapter);
            }
            mActivity.closeKeyboard();
        } else {
            notFavoriteInit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (fav) {
            search_instructions.setText(getString(R.string.search_no_fav));
        } else {
            search_instructions.setText(getString(R.string.directory_instructions));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
