package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MikeD on 11/5/2017.
 */

public class bookGSRActivity extends AppCompatActivity {

    @Bind(R.id.webViewGSR) WebView webView;
    @Bind(R.id.quitBookButton) Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_gsr);

        //get data
        String gsrId = getIntent().getStringExtra("gsrID");
        String gsrLocationCode = getIntent().getStringExtra("gsrLocationCode");

        ButterKnife.bind(this);

        //if user clicks back button, quit activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        webView = (WebView) findViewById(R.id.webViewGSR);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //send post request
        webView.setWebViewClient(new WebViewClient());
        String postData = "tc=done&p1=" + gsrId + "&p2=" + gsrLocationCode + "&p3=8&p4=0&iid=335";
        webView.postUrl("http://libcal.library.upenn.edu/libauth_s_r.php", getBytes(postData, "base64"));



    }

    public static byte[] getBytes(final String data, final String charset) {


        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }


        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }


        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return data.getBytes();
        }
    }

}
