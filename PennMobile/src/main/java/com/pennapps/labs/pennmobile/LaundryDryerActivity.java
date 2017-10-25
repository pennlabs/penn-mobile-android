package com.pennapps.labs.pennmobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LaundryDryerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_machine);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LaundryMachineFragmentNew())
                .commit();
    }
}
