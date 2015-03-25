package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NewsTab extends Fragment {

    WebView mWebView;
    static WebView currentWebView;
    private View mView;
    private boolean mIsWebViewAvailable;
    private boolean newsLoaded;
    boolean isVisibleToUser = false;
    private RelativeLayout Pbar;
    private String mUrl = "http://www.thedp.com/";

    public NewsTab() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsLoaded = false;
        mWebView = new WebView(getActivity());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Bundle args = getArguments();
        mUrl = args.getString("url");

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_news_tab, container, false);

        Pbar = (RelativeLayout) mView.findViewById(R.id.loadingPanel);
        if (!newsLoaded) {
            loadNews();
            newsLoaded = true;
        }

        return mView;
    }

    public void loadNews() {
        mIsWebViewAvailable = true;
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress >= 80) {
                    Pbar.setVisibility(View.GONE);
                    ViewGroup parent = (ViewGroup) mWebView.getParent();
                    if (parent != null) {
                        parent.removeView(mWebView);
                    }
                    ((LinearLayout) mView).addView(mWebView);
                }
            }
        });
        mWebView.setWebViewClient(new InnerWebViewClient()); // forces it to open in app
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            currentWebView = mWebView;
        }
        this.isVisibleToUser = isVisibleToUser;
    }

    /* To ensure links open within the application */
    private class InnerWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
