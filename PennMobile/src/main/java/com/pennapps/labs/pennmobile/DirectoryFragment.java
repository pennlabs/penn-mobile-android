package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.adapters.DirectoryAdapter;
import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;
import com.pennapps.labs.pennmobile.api.Labs;
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

public class DirectoryFragment extends ListFragment {

    private Labs mLabs;
    private MainActivity mActivity;
    private ListView mListView;
    private Context mContext;
    private SearchView searchView;
    private boolean starOn;
    private DirectoryAdapter mainAdapter;
    private Menu mMenu;
    private int stateOfScreen;
    private final int NO_STATE = 1, INSTR_STATE = 0, LIST_STATE = 2;

    @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    @Bind(R.id.no_results) TextView no_results;
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
        loadingPanel.setVisibility(View.GONE);
        stateOfScreen = INSTR_STATE;
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mListView = getListView();
        searchSuggestion(true, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.directory_search:
                return true;
            case R.id.directory_star:
                if (starOn) {
                    starOn = false;
                    item.setIcon(R.drawable.star_off);
                    setScreenWithState();
                } else {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                    Set<String> starred = sharedPref.getStringSet("starred", new HashSet<String>());
                    if (starred.isEmpty()) {
                        Toast.makeText(mContext, "No favorites found.", Toast.LENGTH_SHORT).show();
                    } else {
                        starOn = true;
                        item.setIcon(R.drawable.star_on);
                        if (mListView.getVisibility() == View.GONE) {
                            mListView.setVisibility(View.VISIBLE);
                        }
                        if (no_results.getVisibility() == View.VISIBLE) {
                            no_results.setVisibility(View.GONE);
                        }
                        if (directory_instructions.getVisibility() == View.VISIBLE) {
                            directory_instructions.setVisibility(View.GONE);
                        }
                        List<Person> people = new LinkedList<>();
                        for (String s : starred) {
                            String details = sharedPref.getString(s + ".data", "");
                            if (!details.isEmpty()) {
                                Person person = (new Gson()).fromJson(details, Person.class);
                                people.add(person);
                            }
                        }
                        saveAdapter();
                        DirectoryAdapter adapter = new DirectoryAdapter(mContext, people);
                        mListView.setAdapter(adapter);
                        ((MainActivity) getActivity()).closeKeyboard();
                    }
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setScreenWithState() {
        switch (stateOfScreen) {
            case NO_STATE:
                if (no_results != null) {
                    no_results.setVisibility(View.VISIBLE);
                }
                if (mListView != null) {
                    mListView.setVisibility(View.GONE);
                }
                break;
            case INSTR_STATE:
                if (directory_instructions != null) {
                    directory_instructions.setVisibility(View.VISIBLE);
                }
                if (no_results != null) {
                    no_results.setVisibility(View.GONE);
                }
                if (mListView != null) {
                    mListView.setVisibility(View.GONE);
                }
                break;
            case LIST_STATE:
                if (mainAdapter != null && mListView != null) {
                    mListView.setAdapter(mainAdapter);
                }
                if (no_results != null) {
                    no_results.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);
        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        searchView.setIconified(false);
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
                stateOfScreen = LIST_STATE;
                processQuery(arg0);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryListener);
        mMenu = menu;
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
        searchView.setOnQueryTextFocusChangeListener(focusListener);
        searchView.setOnCloseListener(closeListener);
    }

    private void processQuery(String query) {
        mLabs.people(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Person>>() {
                    @Override
                    public void call(final List<Person> people) {
                        MenuItem star = mMenu.findItem(R.id.directory_star);
                        star.setIcon(R.drawable.star_off);
                        starOn = false;
                        DirectoryAdapter mAdapter = new DirectoryAdapter(mContext, people);
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            if (people.isEmpty()) {
                                if (no_results != null) {
                                    no_results.setVisibility(View.VISIBLE);
                                    stateOfScreen = NO_STATE;
                                    searchSuggestion(false, true);
                                }
                            } else {
                                if (mListView != null) {
                                    mListView.setAdapter(mAdapter);
                                    mListView.setVisibility(View.VISIBLE);
                                    stateOfScreen = LIST_STATE;
                                }
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                            stateOfScreen = LIST_STATE;
                        }
                        if (no_results != null) {
                            no_results.setVisibility(View.VISIBLE);
                            stateOfScreen = NO_STATE;
                        }
                    }
                });
        SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        int index = sharedPref.getInt(getString(R.string.directory_search_count), -1);
        String[] previouskey = getResources().getStringArray(R.array.previous_directory_array);
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
        editor.putInt(getString(R.string.directory_search_count), index);
        editor.putString(previouskey[index], query);
        editor.apply();
        mActivity.closeKeyboard();
    }

    private void saveAdapter(){
        if (!(mListView.getAdapter() instanceof DirectoryAdapter)) {
            mainAdapter = null;
            return;
        }
        DirectoryAdapter current = (DirectoryAdapter) mListView.getAdapter();
        if (current != null) {
            mainAdapter = current;
        }
        if (mListView.getVisibility() != View.VISIBLE) {
            mainAdapter = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.directory);
        mActivity.setNav(R.id.nav_directory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void searchSuggestion(boolean show, boolean gone){
        if (show) {
            final ArrayList<String> list = new ArrayList<>(5);
            SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
            int index = sharedPref.getInt(getString(R.string.directory_search_count), -1);
            if (index != -1) {
                String[] previouskey = getResources().getStringArray(R.array.previous_directory_array);
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
                        if (mListView != null) {
                            mListView.setVisibility(View.VISIBLE);
                            mListView.setAdapter(new ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, list));
                        }
                        if (directory_instructions != null) {
                            directory_instructions.setVisibility(View.GONE);
                        }
                        mActivity.closeKeyboard();
                    }
                }));
            }
        } else {
            if (mainAdapter != null && !gone) {
                mListView.setAdapter(mainAdapter);
            } else {
                mListView.setVisibility(View.GONE);
                no_results.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (!(mListView.getAdapter() instanceof DirectoryAdapter)) {
            String s = mListView.getAdapter().getItem(position).toString();
            processQuery(s);
        }
    }
}
