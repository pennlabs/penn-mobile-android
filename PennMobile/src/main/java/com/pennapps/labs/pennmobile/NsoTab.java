package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pennapps.labs.pennmobile.adapters.NsoAdapter;

import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Jason on 8/11/2016.
 */
public class NsoTab extends SearchFavoriteTab {

    private NsoAdapter adapter;

    private Unbinder unbinder;

    private List<RSSItem> fullList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_search_favorite_tab, container, false);
        unbinder =  ButterKnife.bind(this, v);
        mListView = (ListView) v.findViewById(android.R.id.list);
        initList();
        return v;
    }

    @Override
    public void processQuery (String query) {
        super.processQuery(query);
        processNsoQuery(query);
    }

    private void processNsoQuery(final String query) {
        Observable<List<RSSItem>> obs = Observable.create(new Observable.OnSubscribe<List<RSSItem>>() {
            @Override
            public void call(Subscriber<? super List<RSSItem>> subscriber) {
                subscriber.onNext(getRSSFeed(query));
                subscriber.onCompleted();
            }
        });
        obs.subscribe(new Subscriber<List<RSSItem>>() {
            @Override
            public void onCompleted() {
                if (adapter == null) {
                    no_results.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    mListView.setAdapter(adapter);
                    no_results.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("NSO", "observable error", e);
                no_results.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }

            @Override
            public void onNext(List<RSSItem> rssItems) {
                if (rssItems.isEmpty()) {
                    adapter = null;
                } else {
                    adapter = new NsoAdapter(mActivity, rssItems);
                }
            }
        });
    }

    @NonNull
    private List<RSSItem> getRSSFeed(String query) {
        try {
            List<RSSItem> items;
            if (fullList == null) {
                RSSReader reader = new RSSReader();
                String uri = "http://api.pennlabs.org/nso";
                items = reader.load(uri).getItems();
                fullList = new ArrayList<>(items);
            } else {
                items = new ArrayList<>(fullList);
            }
            if (query.isEmpty()) {
                return items;
            }
            List<RSSItem> answer = new LinkedList<>();
            for (RSSItem item : items) {
                if (item.getTitle().contains(query)) {
                    answer.add(item);
                }
            }
            return answer;
        } catch (RSSReaderException e) {
            Log.d("NSO", "error reading rss", e);
            return new LinkedList<>();
        }
    }


    @Override
    public void initList() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        Set<String> starred = sp.getStringSet(getString(R.string.search_nso_star), new HashSet<String>());
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
        List<RSSItem> items = getRSSFeed("");
        if (fav) {
            // need to improve runtime later (worst case O(n^2) smh
            List<RSSItem> favItems = new LinkedList<>();
            for (String s : starred) {
                String details = sp.getString(s + getString(R.string.search_nso_star), "");
                if (!details.isEmpty()) {
                    for (RSSItem item : items) {
                        if (item.getTitle().equals(details)) {
                            favItems.add(item);
                            break;
                        }
                    }
                }
            }
            items = favItems;
        }
        adapter = new NsoAdapter(mActivity, items);
        mListView.setAdapter(adapter);
        mActivity.closeKeyboard();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (fav) {
            search_instructions.setText(getString(R.string.search_no_fav));
        } else {
            search_instructions.setText(getString(R.string.nso_instructions));
        }
    }
}
