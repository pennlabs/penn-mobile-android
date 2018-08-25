package com.pennapps.labs.pennmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
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
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

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

    /**
     * Given a query for filtering the RSS feed titles, fetches and displays RSS feed in the list view
     * @param query Filter for RSS feed titles
     */
    private void processNsoQuery(final String query) {
        Observable<List<RSSItem>> obs = Observable.create(new Observable.OnSubscribe<List<RSSItem>>() {
            @Override
            public void call(Subscriber<? super List<RSSItem>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(getRSSFeed(query));
                    subscriber.onCompleted();
                }
            }
        });
        // use io thread so main thread is not blocked
        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<RSSItem>>() {
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

    /**
     * Fetches RSS feed filtered by query
     * @param query Filter for RSS feed titles
     * @return List of RSSItem objects
     */
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
            return new LinkedList<>();
        }
    }

    /**
     * Fetches and displays RSS feed in the list view when the NsoTab is initially opened
     */
    @Override
    public void initList() {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final Set<String> starred = sp.getStringSet(getString(R.string.search_nso_star), new HashSet<String>());
        if (search_instructions.getVisibility() == View.VISIBLE) {
            search_instructions.setVisibility(View.GONE);
        }
        if (loadingPanel != null) {
            loadingPanel.setVisibility(View.VISIBLE);
        }
        Single<List<RSSItem>> loadItems = Single.create(new Single.OnSubscribe<List<RSSItem>>() {
            @Override
            public void call(SingleSubscriber<? super List<RSSItem>> singleSubscriber) {
                if (!singleSubscriber.isUnsubscribed()) {
                    singleSubscriber.onSuccess(getRSSFeed(""));
                }
            }
        });

        // use io thread so main thread is not blocked
        loadItems.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    // update the views after loading the RSS item list has failed/succeeded
                    @Override
                    public void call() {
                        if (loadingPanel != null) {
                            loadingPanel.setVisibility(View.GONE);
                        }
                        if (adapter == null) {
                            no_results.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        } else {
                            mListView.setVisibility(View.VISIBLE);
                            mListView.setAdapter(adapter);
                            no_results.setVisibility(View.GONE);
                        }
                    }
                })
                .subscribe(new SingleSubscriber<List<RSSItem>>() {
                    @Override
                    public void onSuccess(List<RSSItem> rssItems) {
                        if (fav) {
                            // need to improve runtime later (worst case O(n^2) smh
                            List<RSSItem> favItems = new LinkedList<>();
                            for (String s : starred) {
                                String details = sp.getString(s + getString(R.string.search_nso_star), "");
                                if (!details.isEmpty()) {
                                    for (RSSItem item : rssItems) {
                                        if (item.getTitle().equals(details)) {
                                            favItems.add(item);
                                            break;
                                        }
                                    }
                                }
                            }
                            rssItems = favItems;
                        }
                        adapter = new NsoAdapter(mActivity, rssItems);
                        mListView.setAdapter(adapter);
                        mActivity.closeKeyboard();
                    }

                    @Override
                    public void onError(Throwable error) {
                    }
                });
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
