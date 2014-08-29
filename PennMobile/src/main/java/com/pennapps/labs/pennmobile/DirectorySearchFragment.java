package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DirectorySearchFragment extends Fragment implements View.OnClickListener {

    private EditText mFirstName;
    private EditText mLastName;
    public static final String FIRST_NAME_INTENT_EXTRA = "FIRST_NAME";
    public static final String LAST_NAME_INTENT_EXTRA = "LAST_NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_directory_search, container, false);
        mFirstName = (EditText) v.findViewById(R.id.directory_first_name);
        mLastName = (EditText) v.findViewById(R.id.directory_last_name);
        Button b = (Button) v.findViewById(R.id.directory_search_button);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        // TODO: error check for filled in fields
        Fragment fragment = new DirectoryFragment();
        Bundle args = new Bundle();
        args.putString(FIRST_NAME_INTENT_EXTRA, mFirstName.getText().toString());
        args.putString(LAST_NAME_INTENT_EXTRA, mLastName.getText().toString());
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

}
