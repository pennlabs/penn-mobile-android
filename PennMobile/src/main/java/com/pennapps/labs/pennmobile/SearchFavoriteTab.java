package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;
import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by Jason on 1/26/2016.
 */
public class SearchFavoriteTab extends ListFragment {

    private boolean fav;
    private String type;
    private ListView mListView;
    private MainActivity mActivity;
    private Labs mLabs;

    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
    @Bind(R.id.search_instructions) TextView search_instructions;


    public SearchFavoriteTab() {
        super();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fav = getArguments().getBoolean(getString(R.string.search_favorite), false);
        type = getArguments().getString(getString(R.string.search_list), "");
        mActivity = (MainActivity) getActivity();
        mLabs = MainActivity.getLabsInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite_tab, container, false);
        ButterKnife.bind(this, v);
        if (fav) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
            Gson gson = new Gson();
            if (type.equals(getString(R.string.registrar, ""))) {
                Set<String> starred = sp.getStringSet(getString(R.string.search_reg_star), new HashSet<String>());
                if (starred.isEmpty()) {
                    Toast.makeText(mActivity, "No favorites found.", Toast.LENGTH_SHORT).show();
                } else {
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
            } else if (type.equals(getString(R.string.directory, ""))) {
                Set<String> starred = sp.getStringSet(getString(R.string.search_dir_star), new HashSet<String>());
                if (starred.isEmpty()) {
                    Toast.makeText(mActivity, "No favorites found.", Toast.LENGTH_SHORT).show();
                } else {
                    if (mListView.getVisibility() == View.GONE) {
                        mListView.setVisibility(View.VISIBLE);
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
                    DirectoryAdapter adapter = new DirectoryAdapter(mActivity, people);
                    mListView.setAdapter(adapter);
                }
            }
            ((MainActivity) getActivity()).closeKeyboard();
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
    }


    public void processQuery(String query) {
        if (search_instructions.getVisibility() == View.VISIBLE) {
            search_instructions.setVisibility(View.GONE);
        }
        if (type.equals(getString(R.string.registrar, ""))) {
            processRegistrarQuery(query);
        } else if (type.equals(getString(R.string.directory, ""))) {
            processDirectoryQuery(query);
        }
    }

    private void processDirectoryQuery(String query) {
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
                                }
                            } else {
                                if (mListView != null) {
                                    DirectoryAdapter mAdapter = new DirectoryAdapter(mActivity, people);
                                    mListView.setAdapter(mAdapter);
                                    mListView.setVisibility(View.VISIBLE);
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

    private void processRegistrarQuery(String query) {
        mLabs.courses(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Course>>() {
                    @Override
                    public void call(List<Course> courses) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            if (courses == null || courses.size() == 0) {
                                no_results.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                RegistrarAdapter mAdapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                                mListView.setVisibility(View.VISIBLE);
                                mListView.setAdapter(mAdapter);
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

    private void noResults() {
        if (loadingPanel != null) {
            loadingPanel.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
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
