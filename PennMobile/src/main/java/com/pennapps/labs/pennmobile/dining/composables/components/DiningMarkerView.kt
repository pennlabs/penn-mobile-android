package com.pennapps.labs.pennmobile.dining.composables.components

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.toColorInt

class DiningMarkerView(
    context: Context,
    private val type: String, // "dollars" or "swipes"
    private val semesterStart: Date
) : MarkerView(context, 0) { // 0 because no XML layout

    private val textView: TextView = TextView(context).apply {
        setPadding(16, 8, 16, 8)
        setBackgroundColor("#FFFFFF".toColorInt())
        setTextColor(if (type == "dollars") "#BADFB8".toColorInt() else "#99BCF7".toColorInt())
        textSize = 14f
    }

    init {
        addView(textView)
    }

    override fun refreshContent(entry: Entry, highlight: Highlight) {
        // Convert X value to date
        val cal = Calendar.getInstance().apply {
            time = semesterStart
            add(Calendar.DAY_OF_YEAR, entry.x.toInt())
        }
        val date = SimpleDateFormat("MMM. d", Locale.US).format(cal.time)
        val valueText = if (type == "dollars") "$${"%.2f".format(entry.y)}" else entry.y.toInt().toString()

        textView.text = "$date\n$valueText"
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        // Position marker above the point
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
