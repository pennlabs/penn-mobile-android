package com.pennapps.labs.pennmobile.dining.composables.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pennapps.labs.pennmobile.dining.classes.DiningInsightCell
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DiningPredictionCard(
    title: String,
    cell: DiningInsightCell,
    extraLabel: String,
    extraAmount: String,
    extraNote: String,
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current

    // Inline conversion of DiningInsightCell to MPAndroidChart entries
    val entries: List<Entry> = cell.diningBalancesList?.diningBalancesList?.mapIndexed { index, balance ->
        val value = when (cell.type) {
            "dining_dollars_predictions" -> balance.diningDollars?.toFloat() ?: 0f
            "dining_swipes_predictions" -> balance.regularVisits?.toFloat() ?: 0f
            else -> 0f
        }
        Entry(index.toFloat(), value)
    } ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.height(8.dp))

        AndroidView(
            factory = {
                LineChart(context).apply {
                    data = LineData(LineDataSet(entries, title))
                    description = Description().apply { text = "" }
                    setTouchEnabled(true)
                    setPinchZoom(true)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = extraLabel)
        Text(text = extraAmount)
        Text(text = extraNote)
    }
}
