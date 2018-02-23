package com.pennapps.labs.pennmobile.adapters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by Jackie on 2017-12-27.
 */

class TimeXAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int time = (int) (value % 12);
        if (value > 12) {
            return time + "p";
        }
        if (value >= 12 && time == 0) {
            return "12p";
        }
        if (time == 0) {
            return "12a";
        }
        return time + "a";
    }
}
