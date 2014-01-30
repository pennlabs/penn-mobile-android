package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class PCRCourseActivity extends ActionBarActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcr_course);
        setCourseInfo();
    }

    private void setCourseInfo() {
        // hard coded for testing
        String mCode = "CIS 110";
        String mName = "Intro to Comp Prog";
        String mAvgCourse = "2.7";
        String mAvgInstr = "2.5";
        String mAvgDiff = "2.8";

        TextView codeTextView = (TextView) findViewById(R.id.course_code);
        codeTextView.setText(mCode);
        TextView nameTextView = (TextView) findViewById(R.id.course_name);
        nameTextView.setText(mName);
        TextView avgCourseTextView = (TextView) findViewById(R.id.course_avg_course);
        avgCourseTextView.setText(mAvgCourse);
        TextView avgInstrTextView = (TextView) findViewById(R.id.course_avg_instr);
        avgInstrTextView.setText(mAvgInstr);
        TextView avgDiffTextView = (TextView) findViewById(R.id.course_avg_diff);
        avgDiffTextView.setText(mAvgDiff);


    }
}