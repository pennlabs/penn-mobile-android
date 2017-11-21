package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class LaundryOnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_onboarding);

        ViewPager viewPager = (ViewPager) findViewById(R.id.laundry_onboarding_view_pager);
        LaundryOnboardingFragmentAdapter adapter = new LaundryOnboardingFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
}
