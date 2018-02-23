package com.pennapps.labs.pennmobile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.api.Labs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BookGsrFragment extends Fragment {

    private Labs mLabs;

    @Bind(R.id.first_name)
    EditText firstName;
    @Bind(R.id.last_name) EditText lastName;
    @Bind(R.id.gsr_email) EditText email;
    @Bind(R.id.submit_gsr)
    Button submit;

    private String gsrID, gsrLocationCode, startTime, endTime;


    public BookGsrFragment() {
        // Required empty public constructor
    }

    public static BookGsrFragment newInstance(String gsrID, String gsrLocationCode, String startTime, String endTime) {
        BookGsrFragment fragment = new BookGsrFragment();
        Bundle args = new Bundle();
        args.putString("gsrID", gsrID);
        args.putString("gsrLocationCode", gsrLocationCode);
        args.putString("startTime", startTime);
        args.putString("endTime", endTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gsrID = getArguments().getString("gsrID");
            gsrLocationCode = getArguments().getString("gsrLocationCode");
            startTime = getArguments().getString("startTime");
            endTime = getArguments().getString("endTime");
        }
        mLabs = MainActivity.getLabsInstance();
        getActivity().setTitle(R.string.gsr);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gsr_details_book, container, false);
        ButterKnife.bind(this, v);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (firstName.getText().toString().matches("") || lastName.getText().toString().matches("")
                        || email.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), "Please fill in all fields before booking",
                            Toast.LENGTH_LONG).show();
                } else {
                    bookGSR(Integer.parseInt(gsrID), Integer.parseInt(gsrLocationCode), startTime, endTime);
                }
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                        Toast.makeText(getActivity(), "GSR successfully booked", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //If any error occurred displaying the error as toast
                        Toast.makeText(getActivity(), "An error has occurred. Please try again." ,Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}
