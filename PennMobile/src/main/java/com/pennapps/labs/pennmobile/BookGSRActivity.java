package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.pennapps.labs.pennmobile.api.Labs;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MikeD on 11/5/2017.
 */

public class BookGSRActivity extends Activity

{

    private Labs mLabs;

    @Bind(R.id.first_name) EditText firstName;
    @Bind(R.id.last_name) EditText lastName;
    @Bind(R.id.gsr_email) EditText email;
    @Bind(R.id.submit_gsr) Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gsr_details_book);

        mLabs = MainActivity.getLabsInstance();

        ButterKnife.bind(this);



        final String gsrId = getIntent().getStringExtra("gsrID");
        final String gsrLocationCode = getIntent().getStringExtra("gsrLocationCode");
        final String startTime = getIntent().getStringExtra("startTime");
        final String endTime = getIntent().getStringExtra("endTime");

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bookGSR(Integer.parseInt(gsrId), Integer.parseInt(gsrLocationCode), startTime, endTime);
            }
        });



    }

    private void bookGSR(int gsrId, int gsrLocationCode, String startTime, String endTime){

        mLabs.bookGSR(

                //Passing the values
                gsrLocationCode,
                gsrId,
                startTime,
                endTime,
                firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                "Penn Mobile GSR",
                "2158986533",
                "2-3",

                //Creating an anonymous callback
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        //On success we will read the server's output using bufferedreader
                        //Creating a bufferedreader object
                        BufferedReader reader = null;

                        //An string to store output from the server
                        String output = "";

                        try {
                            //Initializing buffered reader
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                            //Reading the output in the string
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //Displaying the output as a toast
                        Toast.makeText(getApplicationContext(), "GSR successfully booked", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occured displaying the error as toast
                        Toast.makeText(getApplicationContext(), "An error has occurred. Please try again." ,Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
