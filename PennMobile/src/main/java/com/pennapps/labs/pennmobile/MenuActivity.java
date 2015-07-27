package com.pennapps.labs.pennmobile;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        StringBuilder menuText = new StringBuilder();
        for (Map.Entry<String, HashMap<String, HashSet<String>>> menu : mDiningHall.menus.entrySet()) {
            menuText.setLength(0);
            String mealName = StringUtils.capitalize(menu.getKey());
            for (Map.Entry<String, HashSet<String>> menuItem : menu.getValue().entrySet()) {
                String key = StringUtils.capitalize(menuItem.getKey());
                HashSet<String> items = menuItem.getValue();
                String tab = "&nbsp&nbsp&nbsp ";
                menuText.append("<b>");
                menuText.append(key);
                menuText.append("</b> <br>");
                for (String item : items) {
                    menuText.append(tab);
                    menuText.append(item);
                    menuText.append("<br>");
                }
            }
            // Meal name
            TextView mealNameTV = new TextView(getApplicationContext(), null, R.style.MealName);
            mealNameTV.setVisibility(View.VISIBLE);
            mealNameTV.setText(mealName);
            menuParent.addView(mealNameTV);
            // Menu
            TextView menuTV = new TextView(getApplicationContext(), null, R.style.Menu);
            menuTV.setVisibility(View.VISIBLE);
            menuTV.setText(Html.fromHtml(menuText.toString()));
            menuParent.addView(menuTV);
        }
    }
}
