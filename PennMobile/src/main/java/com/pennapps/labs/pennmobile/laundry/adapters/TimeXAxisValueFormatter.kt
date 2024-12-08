package com.pennapps.labs.pennmobile.laundry.adapters

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * Created by Jackie on 2017-12-27.
 */
internal class TimeXAxisValueFormatter : ValueFormatter() {
    override fun getFormattedValue(
        value: Float,
        axis: AxisBase,
    ): String {
        val time = (value % 12).toInt()
        if (value > 12) {
            return time.toString() + "p"
        }
        if (value >= 12 && time == 0) {
            return "12p"
        }
        return if (time == 0) {
            "12a"
        } else {
            time.toString() + "a"
        }
    }

    override fun getAxisLabel(
        value: Float,
        axis: AxisBase,
    ): String {
        val time = (value % 12).toInt()
        if (value > 12) {
            return time.toString() + "p"
        }
        if (value >= 12 && time == 0) {
            return "12p"
        }
        return if (time == 0) {
            "12a"
        } else {
            time.toString() + "a"
        }
    }
}
