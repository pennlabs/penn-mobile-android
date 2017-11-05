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

import org.apache.http.util.EncodingUtils;

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        String postData = "tc=done&p1=" + gsrId + "&p2=" + gsrLocationCode + "&p3=8&p4=0&iid=335";
        webView.postUrl("http://libcal.library.upenn.edu/libauth_s_r.php", EncodingUtils.getBytes(postData, "base64"));



    }
}
