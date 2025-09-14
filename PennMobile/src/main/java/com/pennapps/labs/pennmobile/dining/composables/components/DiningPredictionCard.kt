package com.pennapps.labs.pennmobile.dining.composables.components

import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.Color as ComposeColor
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.pennapps.labs.pennmobile.dining.classes.DiningInsightCell
import com.pennapps.labs.pennmobile.dining.utils.smoothBalances
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.formatter.ValueFormatter

val diningGreen: Int = "#BADFB8".toColorInt()
val diningBlue: Int = "#99BCF7".toColorInt()
val diningGrey: ComposeColor = ComposeColor("#F5F5F5".toColorInt())
@Composable
fun DiningPredictionCard(
    title: String,
    cell: DiningInsightCell,
    semesterStart: String = "2025-01-15",  // TODO: replace with actual semester start date
    semesterEnd: String = "2025-05-23",    // TODO: replace with actual semester end date
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val startDate = sdf.parse(semesterStart)!!
    val endDate = sdf.parse(semesterEnd)!!

    val rawValues = cell.diningBalancesList?.diningBalancesList?.mapNotNull { balance ->
        when (cell.type) {
            "dining_dollars_predictions" -> balance.diningDollars?.toFloat()
            "dining_swipes_predictions" -> balance.regularVisits?.toFloat()
            else -> null
        }
    } ?: emptyList()

    val dateFormatter = object : ValueFormatter() {
        private val displayFormat = SimpleDateFormat("MMM. d", Locale.US) // "Mar. 20"

        override fun getFormattedValue(value: Float): String {
            val cal = Calendar.getInstance().apply {
                time = startDate
                add(Calendar.DAY_OF_YEAR, value.toInt())
            }
            return displayFormat.format(cal.time)
        }
    }

    val smoothedValues = smoothBalances(rawValues)

    // Convert balances to chart entries (x = days since semester start, y = balance)
    val entries = cell.diningBalancesList?.diningBalancesList?.mapIndexedNotNull { index, balance ->
        val dateString = balance.date ?: return@mapIndexedNotNull null
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString) ?: return@mapIndexedNotNull null
        val daysFromStart = ((date.time - startDate.time) / (1000 * 60 * 60 * 24)).toFloat()
        Entry(daysFromStart, smoothedValues[index])
    } ?: emptyList()

    // Build prediction line using simple slope
    val predictionEntries: List<Entry> = if (entries.size >= 2) {
        val first = entries.first()
        val last = entries.last()
        val slope = (last.y - first.y) / (last.x - first.x)
        val intercept = first.y - slope * first.x
        val endX = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt()
        (last.x.toInt()..endX).map { day ->
            Entry(day.toFloat(), slope * day + intercept)
        }
    } else emptyList()

    // Compute out-of-funds date
    val outOfFundsDate: String? = predictionEntries.firstOrNull { it.y <= 0 }?.x?.toInt()?.let { days ->
        val cal = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.DAY_OF_YEAR, days)
        }
        SimpleDateFormat("MMM d", Locale.US).format(cal.time)
    }

    Card (
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = diningGrey
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                factory = {
                    LineChart(context).apply {
                        // Actual line
                        xAxis.valueFormatter = dateFormatter
                        xAxis.labelRotationAngle = -45f
                        var actualSet = LineDataSet(entries, "Actual").apply {}
                        if (cell.type!!.contains("dollars")) {
                            actualSet = LineDataSet(entries, "Actual").apply {
                                color = diningGreen
                                setDrawCircles(true)
                                setDrawCircleHole(false)
                                circleColors = listOf(diningGreen)
                                setDrawValues(false)
                                lineWidth = 4f
                            }
                        } else {
                            actualSet = LineDataSet(entries, "Actual").apply {
                                color = diningBlue
                                setDrawCircles(true)
                                setDrawCircleHole(false)
                                circleColors = listOf(diningBlue)
                                setDrawValues(false)
                                lineWidth = 4f
                            }
                        }

                        // Prediction line
                        val predictionSet = LineDataSet(predictionEntries, "Prediction").apply {
                            color = AndroidColor.GRAY
                            setDrawCircles(false)
                            setDrawValues(false)
                            enableDashedLine(10f, 5f, 0f)
                            lineWidth = 4f
                        }

                        data = LineData(actualSet, predictionSet)

                        // Add vertical red line for end of term
                        val endX = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toFloat()
                        val ll = LimitLine(endX, "End of Term").apply {
                            lineColor = AndroidColor.RED
                            lineWidth = 2f
                            textColor = AndroidColor.RED
                            textSize = 12f
                        }
                        xAxis.addLimitLine(ll)

                        // Style chart
                        description = Description().apply { text = "" }
                        setTouchEnabled(true)
                        setPinchZoom(true)
                        axisRight.isEnabled = false
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)
                        axisLeft.setDrawGridLines(false)
                        axisLeft.axisMinimum = 0f
                        legend.isEnabled = false

                        invalidate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (outOfFundsDate != null) {
                Text(text = "Out of ${if (cell.type!!.contains("dollars")) "Dollars" else "Swipes"} $outOfFundsDate")
            } else {
                Text(text = "Balance lasts through end of term")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
