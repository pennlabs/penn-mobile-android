package com.pennapps.labs.pennmobile;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.adapters.RegistrarAdapter;

public class DirectorySearchFragment extends Fragment {

    public static final String FIRST_NAME_INTENT_EXTRA = "FIRST_NAME";
    public static final String LAST_NAME_INTENT_EXTRA = "LAST_NAME";
    private SearchView searchView;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_directory, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.directory_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.directory, menu);

        searchView = (SearchView) menu.findItem(R.id.directory_search).getActionView();
        final SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String arg0) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO: error check for filled in fields
                Fragment fragment = new DirectoryFragment();
                Bundle args = new Bundle();
                String[] query = arg0.split("\\s+");
                if (query.length == 0) {
                    args.putString(FIRST_NAME_INTENT_EXTRA, "");
                    args.putString(FIRST_NAME_INTENT_EXTRA, "");
                } else if (query.length == 1) {
                    args.putString(FIRST_NAME_INTENT_EXTRA, query[0].replaceAll("\\s+",""));
                    args.putString(LAST_NAME_INTENT_EXTRA, "");
                } else {
                    args.putString(FIRST_NAME_INTENT_EXTRA, query[0].replaceAll("\\s+",""));
                    args.putString(LAST_NAME_INTENT_EXTRA, query[1].replaceAll("\\s+",""));
                }
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                return true;
            }
        };
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(queryListener);
    }

}
