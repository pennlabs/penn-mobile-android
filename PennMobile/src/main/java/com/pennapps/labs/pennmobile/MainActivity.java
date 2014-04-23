package com.pennapps.labs.pennmobile;

import android.content.Intent;
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

        mFeatureTitles = new String[] {"Transit", "Directory"};

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mFeatureTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Fragment fragment = new DirectorySearchFragment();
        Fragment fragment = new RegistrarSearchFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mFeatureTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void setTitle(CharSequence title) {
        getActionBar().setTitle(title);
    }

    /*
    public void startDirectory(View v) {
        Intent intent = new Intent(this, DirectorySearchActivity.class);
        startActivity(intent);
    }


    public void startCourseSearch(View v) {
        Intent intent = new Intent(this, RegistrarSearchActivity.class);
        startActivity(intent);
    }
    */
}
