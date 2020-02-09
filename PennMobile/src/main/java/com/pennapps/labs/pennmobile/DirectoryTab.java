package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        setMListView((ListView) v.findViewById(android.R.id.list));
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
        getMLabs().people(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(final List<Person> people) {
                        getLoadingPanel().setVisibility(View.GONE);
                        if (people.isEmpty()) {
                            getNo_results().setVisibility(View.VISIBLE);
                            getMListView().setVisibility(View.GONE);
                        } else {
                            mAdapter = new DirectoryAdapter(getMActivity(), people);
                            getMListView().setAdapter(mAdapter);
                            getMListView().setVisibility(View.VISIBLE);
                            getNo_results().setVisibility(View.GONE);
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
        if (getFav()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getMActivity());
            Gson gson = new Gson();
            Set<String> starred = sp.getStringSet(getString(R.string.search_dir_star), new HashSet<String>());
            if (starred.isEmpty()) {
                notFavoriteInit();
            } else {
                if (getLoadingPanel().getVisibility() == View.VISIBLE) {
                    getLoadingPanel().setVisibility(View.GONE);
                }
                if (getMListView().getVisibility() == View.GONE) {
                    getMListView().setVisibility(View.VISIBLE);
                }
                if (getNo_results().getVisibility() == View.VISIBLE) {
                    getNo_results().setVisibility(View.GONE);
                }
                if (getSearch_instructions().getVisibility() == View.VISIBLE) {
                    getSearch_instructions().setVisibility(View.GONE);
                }
                List<Person> people = new LinkedList<>();
                for (String s : starred) {
                    String details = sp.getString(s + getString(R.string.search_dir_star), "");
                    if (!details.isEmpty()) {
                        Person person = gson.fromJson(details, Person.class);
                        people.add(person);
                    }
                }
                mAdapter = new DirectoryAdapter(getMActivity(), people);
                getMListView().setAdapter(mAdapter);
                getMListView().setOnItemClickListener(null);
            }
            getMActivity().closeKeyboard();
        } else {
            notFavoriteInit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getFav()) {
            getSearch_instructions().setText(getString(R.string.search_no_fav));
        } else {
            getSearch_instructions().setText(getString(R.string.directory_instructions));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
