package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.api.Labs;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;


/**
 * Created by Jason on 1/26/2016.
 */
public abstract class SearchFavoriteTab extends ListFragment {

    protected boolean fav;
    protected String type;
    protected ListView mListView;
    protected MainActivity mActivity;
    protected Labs mLabs;

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    protected @BindView(R.id.loadingPanel) RelativeLayout loadingPanel;
    protected @BindView(R.id.no_results) TextView no_results;
    protected @BindView(R.id.search_instructions) TextView search_instructions;


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
        if (search_instructions.getVisibility() == View.VISIBLE && !query.isEmpty()) {
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
        if (search_instructions != null) {
            search_instructions.setVisibility(View.VISIBLE);
            no_results.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
    }

    public abstract void initList();

    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
