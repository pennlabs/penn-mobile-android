package com.pennapps.labs.pennmobile;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mFeatureTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFeatureTitles = new String[] {"Registrar", "Directory", "Transit", "Dining"};

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mFeatureTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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
            fragment = new RegistrarSearchFragment();
        } else if (position == 1) {
            fragment = new DirectorySearchFragment();
        } else if (position == 2) {
            fragment = new TransitExpListFragment();
        } else if (position == 3) {
            fragment = new DiningFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mFeatureTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void setTitle(CharSequence title) {
        getActionBar().setTitle("PennMobile: " + title);
    }
}
