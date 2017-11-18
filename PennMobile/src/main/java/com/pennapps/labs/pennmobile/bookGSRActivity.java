package com.pennapps.labs.pennmobile;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TimePicker;


import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * Created by MikeD on 11/5/2017.
 */

public class bookGSRActivity extends AppCompatActivity {

    WebView webView;

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_gsr);

        //get data
        String gsrId = getIntent().getStringExtra("gsrID");
        String gsrLocationCode = getIntent().getStringExtra("gsrLocationCode");

        backButton = (Button) findViewById(R.id.quitBookButton);

        //if user clicks back button, quit activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });


        webView = (WebView) findViewById(R.id.webView);

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
