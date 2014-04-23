package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DirectorySearchFragment extends Fragment {

    private EditText mFirstName;
    private EditText mLastName;
    public static final String FIRST_NAME_INTENT_EXTRA = "FIRST_NAME";
    public static final String LAST_NAME_INTENT_EXTRA = "LAST_NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_directory_search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_directory_search, container, false);
        mFirstName = (EditText) v.findViewById(R.id.directory_first_name);
        mLastName = (EditText) v.findViewById(R.id.directory_last_name);
        return v;
    }

    /*
    public void searchDirectory(View view) {
        // TODO: error check for filled in fields
        Intent intent = new Intent(this, DirectoryActivity.class);
        intent.putExtra(FIRST_NAME_INTENT_EXTRA, mFirstName.getText().toString());
        intent.putExtra(LAST_NAME_INTENT_EXTRA, mLastName.getText().toString());
        startActivity(intent);
    }
    */
}
