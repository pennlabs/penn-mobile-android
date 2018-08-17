package com.pennapps.labs.pennmobile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ViewFlipper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NewsTab extends Fragment {

    @BindView(R.id.flipper) ViewFlipper mFlipper;
    @BindView(R.id.webViewNews) WebView mWebView;

    private Unbinder unbinder;
    private String mUrl = "http://www.thedp.com/";
    static WebView currentWebView;
    private boolean newsLoaded;

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
        unbinder = ButterKnife.bind(this, v);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        if (!newsLoaded) {
            loadNews();
            newsLoaded = true;
        }
        return v;
    }

    public void loadNews() {
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        boolean isVisible = getUserVisibleHint();
        if (isVisible) {
            currentWebView = mWebView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
}
