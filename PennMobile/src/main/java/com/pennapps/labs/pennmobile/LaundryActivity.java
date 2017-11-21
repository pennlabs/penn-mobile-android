package com.pennapps.labs.pennmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class LaundryActivity extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);

        // onboarding
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // first time
        if (sp.getBoolean("laundryNew", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("laundryNew", false);
            editor.apply();

            Intent intent = new Intent(this, LaundryOnboardingActivity.class);
            startActivity(intent);
            finish();
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        LaundryFragmentAdapter adapter = new LaundryFragmentAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.laundry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.laundry_settings) {
            Intent intent = new Intent(this, com.pennapps.labs.pennmobile.LaundrySettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}