package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Jason on 2/1/2016.
 */
public class RegistrarTab extends SearchFavoriteTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite_tab, container, false);
        ButterKnife.bind(this, v);
        mListView = (ListView) v.findViewById(android.R.id.list);
        if (fav) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            Gson gson = new Gson();
            Set<String> starred = sp.getStringSet(getString(R.string.search_reg_star), new HashSet<String>());
            if (starred.isEmpty()) {
                Toast.makeText(mActivity, "No favorites found.", Toast.LENGTH_SHORT).show();
                notFavoriteInit();
            } else {
                if (loadingPanel.getVisibility() == View.VISIBLE) {
                    loadingPanel.setVisibility(View.GONE);
                }
                if (mListView.getVisibility() == View.GONE) {
                    mListView.setVisibility(View.VISIBLE);
                }
                if (no_results.getVisibility() == View.VISIBLE) {
                    no_results.setVisibility(View.GONE);
                }
                if (search_instructions.getVisibility() == View.VISIBLE) {
                    search_instructions.setVisibility(View.GONE);
                }
                List<Course> courses = new LinkedList<>();
                for (String s : starred) {
                    String details = sp.getString(s + getString(R.string.search_reg_star), "");
                    if (!details.isEmpty()) {
                        Course course = gson.fromJson(details, Course.class);
                        courses.add(course);
                    }
                }
                RegistrarAdapter adapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                mListView.setAdapter(adapter);
            }
            ((MainActivity) getActivity()).closeKeyboard();
        } else {
            notFavoriteInit();
        }
        return v;
    }

    @Override
    public void processQuery (String query) {
        super.processQuery(query);
        processRegistrarQuery(query);
    }

    private void processRegistrarQuery(String query) {
        mLabs.courses(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Course>>() {
                    @Override
                    public void call(List<Course> courses) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            if (courses.isEmpty()) {
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (mListView != null) {
                                    RegistrarAdapter mAdapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                                    mListView.setVisibility(View.VISIBLE);
                                    mListView.setAdapter(mAdapter);
                                }
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

    private List<Course> filterCourses(List<Course> courses){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean recitations = sharedPref.getBoolean("pref_recitations", true);
        if (!recitations) {
            List<Course> courses_filt = new ArrayList<>();
            for (Course course : courses) {
                if (course.activity.equals("LEC")) {
                    courses_filt.add(course);
                }
            }
            return courses_filt;
        }else{
            return courses;
        }
    }
}
