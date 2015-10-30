package com.pennapps.labs.pennmobile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsTab extends Fragment {

    static WebView currentWebView;
    private boolean mIsWebViewAvailable;
    boolean isVisibleToUser = false;
    private boolean newsLoaded;
    private String mUrl = "http://www.thedp.com/";

    @Bind(R.id.webview) WebView mWebView;
    @Bind(R.id.flipper) ViewFlipper mFlipper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsLoaded = false;

        Bundle args = getArguments();
        mUrl = args.getString("url");

        ((MainActivity) getActivity()).closeKeyboard();
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_tab, container, false);
        ButterKnife.bind(this, v);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        if (!newsLoaded) {
            loadNews();
            newsLoaded = true;
        }
        return v;
    }

    public void loadNews() {
        mIsWebViewAvailable = true;
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                if (progress >= 80 && mWebView != null) {
                    mFlipper.setDisplayedChild(1);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient()); // forces it to open in app
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        boolean isVisible = getUserVisibleHint();
        if (isVisible) {
            currentWebView = mWebView;
        }
        this.isVisibleToUser = isVisible;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        getActivity().setTitle(R.string.news);
        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
}
