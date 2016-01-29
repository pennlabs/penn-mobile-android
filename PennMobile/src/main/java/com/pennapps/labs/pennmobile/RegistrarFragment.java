package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.Course;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RegistrarFragment extends ListFragment {

    private Labs mLabs;
    private ListView listView;
    private MainActivity mActivity;
    private RegistrarAdapter mAdapter;
    public static SearchView searchView;
    public static boolean hideSearchView = false;

    @Bind(R.id.no_results) TextView no_results;
    @Bind(R.id.registrar_instructions) TextView registrar_instructions;
    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mLabs = MainActivity.getLabsInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_registrar, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        listView = getListView();
        searchSuggestion(true, false);
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
        if (hideSearchView) {
            searchView.clearFocus();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.registrar, menu);
        loadingPanel.setVisibility(View.GONE);
        final MenuItem searchMenuItem = menu.findItem(R.id.registrar_search);
        searchMenuItem.expandActionView();

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setIconified(false);
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String input) {
                searchView.clearFocus();
                registrar_instructions.setVisibility(View.GONE);
                no_results.setVisibility(View.GONE);
                loadingPanel.setVisibility(View.VISIBLE);
                searchCourses(input);
                return true;
            }
        };

        final View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange (View v, boolean hasFocus) {
                if (hasFocus) {
                    searchSuggestion(true, false);
                }
            }
        };

        final SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                searchSuggestion(false, false);
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
        searchView.setOnQueryTextFocusChangeListener(focusListener);
        searchView.setOnCloseListener(closeListener);
    }

    private void searchCourses(String query) {
        mLabs.courses(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Course>>() {
                    @Override
                    public void call(List<Course> courses) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            if (courses == null || courses.size() == 0) {
                                no_results.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
                                searchSuggestion(false, true);
                            } else {
                                mAdapter = new RegistrarAdapter(mActivity, filterCourses(courses));
                                listView.setVisibility(View.VISIBLE);
                                listView.setAdapter(mAdapter);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            no_results.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        }
                    }
                });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int index = sharedPref.getInt(getString(R.string.registrar_search_count), -1);
        String[] previouskey = getResources().getStringArray(R.array.previous_course_array);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (index != -1) {
            boolean changed = false;
            for (int i = 4; i >= 0; i--) {
                int id = (index + 5 - i) % 5;
                String s = sharedPref.getString(previouskey[id], "");
                if (s.equals(query)) {
                    changed = true;
                }
                if (changed && i > 0) {
                    int prev = (index + 5 - i + 1) % 5;
                    editor.putString(previouskey[id], sharedPref.getString(previouskey[prev], ""));
                }
            }
            if (changed) {
                editor.putString(previouskey[index], query);
                editor.apply();
                return;
            }
        }
        index = (index + 1) % 5;
        editor.putInt(getString(R.string.registrar_search_count), index);
        editor.putString(previouskey[index], query);
        editor.apply();
        mActivity.closeKeyboard();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!(listView.getAdapter() instanceof RegistrarAdapter)) {
            String s = listView.getAdapter().getItem(position).toString();
            searchCourses(s);
            mActivity.closeKeyboard();
            return;
        }
        Fragment fragment = new CourseFragment();
        Course course = ((RegistrarAdapter.ViewHolder) v.getTag()).course;
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(false);
        mActivity.getActionBarToggle().syncState();
        Bundle args = new Bundle();
        args.putParcelable("CourseFragment", course);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.registrar_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registrar);
        mActivity.setNav(R.id.nav_registrar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    private void searchSuggestion(boolean show, boolean gone){
        if (show) {
            final ArrayList<String> list = new ArrayList<>(5);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int index = sharedPref.getInt(getString(R.string.registrar_search_count), -1);
            if (index != -1) {
                String[] previouskey = getResources().getStringArray(R.array.previous_course_array);
                for (int i = 0; i < 5; i++){
                    int id = (index + 5 - i) % 5;
                    String previous = sharedPref.getString(previouskey[id], "");
                    if (!previous.isEmpty()) {
                        list.add(previous);
                    }
                }
            }
            if (!list.isEmpty()) {
                mActivity.runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                        }
                        if (no_results != null){
                            no_results.setVisibility(View.GONE);
                        }
                        if (listView != null) {
                            listView.setVisibility(View.VISIBLE);
                            listView.setAdapter(new ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, list));
                        }
                        if (registrar_instructions != null) {
                            registrar_instructions.setVisibility(View.GONE);
                        }
                        mActivity.closeKeyboard();
                    }
                }));
            }
        } else {
            if (mAdapter != null && !gone) {
                listView.setAdapter(mAdapter);
            } else {
                listView.setVisibility(View.GONE);
                no_results.setVisibility(View.VISIBLE);
            }
        }
    }
}


