package com.pennapps.labs.pennmobile.adapters

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

/**
 * Created by Jackie on 2017-12-27.
 */
internal class TimeXAxisValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val time = (value % 12).toInt()
        if (value > 12) {
            return time.toString() + "p"
        }
        if (value >= 12 && time == 0) {
            return "12p"
        }
        return if (time == 0) {
            "12a"
        } else time.toString() + "a"
    }
}