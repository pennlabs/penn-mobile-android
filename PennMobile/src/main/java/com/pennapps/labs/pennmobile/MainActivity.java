package com.pennapps.labs.pennmobile;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.res.Configuration;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;

public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mFeatureTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        mFeatureTitles = new String[] {"Home", "Registrar", "Directory", "Dining", "Transit", "News"};

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mFeatureTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set default fragment to MainFragment
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new MainFragment());
        tx.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new MainFragment();
        } if (position == 1) {
            fragment = new RegistrarSearchFragment();
        } else if (position == 2) {
            fragment = new DirectorySearchFragment();
        } else if (position == 3) {
            fragment = new DiningFragment();
        } else if (position == 4) {
            fragment = new TransitFragment();
        } else if (position == 5) {
            fragment = new EventsFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mFeatureTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void onClick(View v) {
        // Click event handler for the main menu on first open
        CharSequence viewText = ((TextView) v).getText();
        if (viewText.equals("Registrar")) {
            selectItem(1);
        } else if (viewText.equals("Directory")) {
            selectItem(2);
        } else if (viewText.equals("Dining")) {
            selectItem(3);
        } else if (viewText.equals("Transit")) {
            selectItem(4);
        } else if (viewText.equals("News")) {
            selectItem(5);
        }
    }

    public void onHomeButtonClick(View v) {
        if (v.getId() == R.id.registrar_img || v.getId() == R.id.registrar_cont) {
            selectItem(1);
        } else if (v.getId() == R.id.directory_img || v.getId() == R.id.directory_cont) {
            selectItem(2);
        } else if (v.getId() == R.id.dining_img || v.getId() == R.id.dining_cont) {
            selectItem(3);
        } else if (v.getId() == R.id.transit_img || v.getId() == R.id.transit_cont) {
            selectItem(4);
        } else if (v.getId() == R.id.news_img || v.getId() == R.id.transit_cont) {
            selectItem(5);
        }
    }

    public void setTitle(CharSequence title) {
        try {
            getActionBar().setTitle(title);
        } catch (NullPointerException e) {
            getActionBar().setTitle("PennMobile");
        }
    }
}
