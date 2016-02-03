package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
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
public abstract class SearchFavoriteTab extends ListFragment {

    protected boolean fav;
    protected String type;
    protected ListView mListView;
    protected MainActivity mActivity;
    protected Labs mLabs;

    protected @Bind(R.id.loadingPanel) RelativeLayout loadingPanel;
    protected @Bind(R.id.no_results) TextView no_results;
    protected @Bind(R.id.search_instructions) TextView search_instructions;


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

    public void processQuery(String query) {
        if (search_instructions.getVisibility() == View.VISIBLE && !query.equals("")) {
            search_instructions.setVisibility(View.GONE);
            if(loadingPanel != null) {
                loadingPanel.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void noResults() {
        if (loadingPanel != null) {
            loadingPanel.setVisibility(View.GONE);
            no_results.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            search_instructions.setVisibility(View.GONE);
        }
    }

    protected void notFavoriteInit() {
        search_instructions.setVisibility(View.VISIBLE);
        no_results.setVisibility(View.GONE);
        loadingPanel.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
    }

    public abstract void initList();
}
