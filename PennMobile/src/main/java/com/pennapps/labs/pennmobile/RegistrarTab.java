package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

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

    private RegistrarAdapter mAdapter;
    private int frameID;
    public static CourseFragment[] fragments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragments == null) {
            fragments = new CourseFragment[2];
        }
        View v = inflater.inflate(R.layout.fragment_search_favorite_tab, container, false);
        FrameLayout frameLayout = (FrameLayout) v.findViewById(R.id.search_fav_frame);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            frameID = generateViewId();
        } else {
            frameID = View.generateViewId();
        }
        frameLayout.setId(frameID);

        ButterKnife.bind(this, v);
        mListView = (ListView) v.findViewById(android.R.id.list);
        initList();
        setBackButton(frameLayout);
        return v;
    }

    private void setBackButton(FrameLayout frameLayout) {
        frameLayout.setFocusableInTouchMode(true);
        frameLayout.requestFocus();
        frameLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && fragments[fav ? 1 : 0] != null) {
                    FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(fragments[fav ? 1 : 0]).commit();
                    fragments[fav ? 1 : 0] = null;
                    return true;
                }
                if (((fav && fragments[0] != null && fragments[1] == null) || (!fav && fragments[0] == null && fragments[1] != null)) &&
                        (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(fragments[fav ? 0 : 1]).commit();
                    fragments[fav ? 0 : 1] = null;
                }
                return false;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!(mListView.getAdapter() instanceof RegistrarAdapter)) {
            String s = mListView.getAdapter().getItem(position).toString();
            processRegistrarQuery(s);
            mActivity.closeKeyboard();
            return;
        }
        int pos = fav ? 1 : 0;
        fragments[pos] = new CourseFragment();
        Course course = ((RegistrarAdapter.ViewHolder) v.getTag()).course;
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.course_bundle_arg), course);
        args.putBoolean(getString(R.string.registrar_search), fav);
        fragments[pos].setArguments(args);

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(frameID, fragments[pos])
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void processQuery (String query) {
        super.processQuery(query);
        processRegistrarQuery(query);
    }

    private void processRegistrarQuery(String query) {
        if (query.isEmpty()) {
            return;
        }
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
                                    mListView.setVisibility(View.GONE);
                                }
                            } else {
                                if (mListView != null) {
                                    mAdapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                                    mListView.setVisibility(View.VISIBLE);
                                    mListView.setAdapter(mAdapter);
                                    no_results.setVisibility(View.GONE);
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

    public void initList() {
        if (fav) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            Gson gson = new Gson();
            Set<String> starred = sp.getStringSet(getString(R.string.search_reg_star), new HashSet<String>());
            if (starred.isEmpty()) {
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
                mAdapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                mListView.setAdapter(mAdapter);
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
            search_instructions.setText(getString(R.string.registrar_instructions));
        }
    }
}
