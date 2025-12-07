package com.pennapps.labs.pennmobile.dining.classes

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.adapters.DiningInsightsCardAdapter
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class DiningMarkerView(
    context: Context,
    layoutResource: Int,
) : MarkerView(context, layoutResource) {
    companion object {
        private const val DINING_DOLLARS_PREDICTIONS = 2
        private const val DINING_SWIPES_PREDICTIONS = 3
    }

    private val textView: TextView = findViewById(R.id.dining_marker_text)
    private val point: ImageView = findViewById(R.id.dining_marker_point)

    private var xValue: Float = 0.0F
    private var yValue: Float = 0.0F
    private var typeId: Int = 0

    // Callback method to update the marker content
    override fun refreshContent(
        entry: Entry,
        highlight: Highlight,
    ) {
        point.visibility = GONE // Remove this once it's properly aligned
        xValue = entry.x
        yValue = entry.y
        // convert entry.x to date
        val daysFromStart = xValue.roundToInt()
        val sdf = SimpleDateFormat("MMM. dd")
        val date = DiningInsightsCardAdapter.Utils.addDaysToDateMMMdd(DiningInsightsCardAdapter.START_DAY_OF_SEMESTER, daysFromStart)

        var diningData = String.format("%.2f", yValue)
        if (typeId == DINING_SWIPES_PREDICTIONS) {
            diningData = yValue.toInt().toString()
        }
        if (typeId == DINING_DOLLARS_PREDICTIONS) {
            diningData = "\$" + diningData
        }
        textView.text =
            buildString {
                append(date)
                append("\n")
                append(diningData)
            }
        val isDark = (context.resources.configuration.uiMode
                and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
        textView.setTextColor(textColor)

        if (typeId == DINING_SWIPES_PREDICTIONS) {
            point.setColorFilter(context.getColor(R.color.diningBlue))
        } else {
            point.setColorFilter(context.getColor(R.color.diningGreen))
        }
    }

    fun setGraphType(typeId: Int) {
        this.typeId = typeId
    }

    // This is used to reposition the marker. Please fix if we want this to be visible
    override fun getOffset(): MPPointF {
        // Center horizontally: -(width / 2)
        // Position closer to the point: reduce the padding
        return MPPointF(-(width / 2f), (-(height/6)).toFloat())
    }
}
