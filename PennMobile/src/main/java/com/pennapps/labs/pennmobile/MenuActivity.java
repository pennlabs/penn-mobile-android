package com.pennapps.labs.pennmobile;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.DiningHall;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {

    private DiningHall mDiningHall;

    @Bind(R.id.dining_hall_name) TextView hallNameTV;
    @Bind(R.id.dining_hall_status) TextView hallStatus;
    @Bind(R.id.menu_parent) LinearLayout menuParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        mDiningHall = getIntent().getExtras().getParcelable("DiningHall");
        fillDescriptions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case(R.id.action_settings):
                return true;
            case(android.R.id.home):
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fillDescriptions() {
        hallNameTV.setText(WordUtils.capitalizeFully(mDiningHall.getName()));
        if (mDiningHall.isOpen()) {
            hallStatus.setText("Open");
            hallStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.label_green));
        } else {
            hallStatus.setText("Closed");
            hallStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.label_red));
        }

        for (Map.Entry<String, HashMap<String, HashSet<String>>> menu : mDiningHall.menus.entrySet()) {
            addDiningTextView(R.style.MealName, StringUtils.capitalize(menu.getKey()));
            for (Map.Entry<String, HashSet<String>> menuItem : menu.getValue().entrySet()) {
                addDiningTextView(R.style.DiningStation, StringUtils.capitalize(menuItem.getKey()));
                for (String item : menuItem.getValue()) {
                    addDiningTextView(R.style.FoodItem, item);
                }
            }
        }
    }

    private void addDiningTextView(int style, String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setTextAppearance(getApplicationContext(), style);
        textView.setText(text);
        if (style == R.style.FoodItem) {
            textView.setPadding(50, 0, 0, 0);
        } else if (style == R.style.MealName) {
            textView.setPadding(0, 25, 0, 25);
        }
        menuParent.addView(textView);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.menu);
    }
}
