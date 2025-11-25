package com.pennapps.labs.pennmobile.dining.composables.components

import GilroyBold
import GilroyExtraBold
import GilroyLight
import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.classes.DiningInsightCell
import com.pennapps.labs.pennmobile.dining.classes.DiningMarkerView
import com.pennapps.labs.pennmobile.dining.utils.smoothBalances
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

val diningGreen: Int = "#BADFB8".toColorInt()
val diningBlue: Int = "#99BCF7".toColorInt()
val diningGrey: ComposeColor = ComposeColor("#F5F5F5".toColorInt())

@Composable
fun DiningPredictionCard(
    title: String,
    cell: DiningInsightCell,
    modifier: Modifier = Modifier,
    semesterStart: String = "2025-01-15",
    semesterEnd: String = "2025-05-13",
) {
    val context: Context = LocalContext.current
    var selectedInfo by remember { mutableStateOf<String?>(null) }
    var chartInstance by remember { mutableStateOf<LineChart?>(null) }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
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
        private val displayFormat = SimpleDateFormat("MMM. d", Locale.US)

        override fun getFormattedValue(value: Float): String {
            val cal = Calendar.getInstance().apply {
                time = startDate
                add(Calendar.DAY_OF_YEAR, value.toInt())
            }
            return displayFormat.format(cal.time)
        }
    }

    val smoothedValues = smoothBalances(rawValues)

    val entries = cell.diningBalancesList?.diningBalancesList?.mapIndexedNotNull { index, balance ->
        val dateString = balance.date ?: return@mapIndexedNotNull null
        val date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(dateString)
        } catch (e: Exception) {
            null
        } ?: return@mapIndexedNotNull null

        // Filter out dates after semester end
        if (date.after(endDate)) return@mapIndexedNotNull null

        if (index >= smoothedValues.size) return@mapIndexedNotNull null
        val daysFromStart = ((date.time - startDate.time) / (1000 * 60 * 60 * 24)).toFloat()

        // Also filter out negative days (dates before semester start)
        if (daysFromStart < 0) return@mapIndexedNotNull null

        Entry(daysFromStart, smoothedValues[index])
    } ?: emptyList()

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

    val outOfFundsDate: String? = predictionEntries.firstOrNull { it.y <= 0 }?.x?.toInt()?.let { days ->
        val cal = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.DAY_OF_YEAR, days)
        }
        SimpleDateFormat("MMM. d", Locale.US).format(cal.time)
    }

    val predictedEndBalance: Float? = if (entries.size >= 2) {
        val first = entries.first()
        val last = entries.last()
        val slope = (last.y - first.y) / (last.x - first.x)
        val intercept = first.y - slope * first.x
        val endX = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toFloat()
        slope * endX + intercept
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                chartInstance?.highlightValue(null)
                selectedInfo = null
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val colorScheme = MaterialTheme.colorScheme
            val isDark = isSystemInDarkTheme()
            var textColor = colorScheme.onSurface.toArgb()
            val bgColor = colorScheme.surface.toArgb()

            AndroidView(
                factory = {
                    LineChart(context).apply {
                        chartInstance = this
                        setBackgroundColor(bgColor)
                        clipToPadding = false

                        xAxis.textColor = textColor
                        axisLeft.textColor = textColor
                        legend.textColor = textColor
                        description.textColor = textColor
                        xAxis.axisLineColor = textColor
                        axisLeft.axisLineColor = textColor

                        val gridColor = if (isDark) AndroidColor.DKGRAY else AndroidColor.LTGRAY
                        xAxis.gridColor = gridColor
                        axisLeft.gridColor = gridColor

                        xAxis.valueFormatter = dateFormatter
                        xAxis.labelRotationAngle = -45f

                        val actualSet = LineDataSet(entries, "Actual").apply {
                            color = if (cell.type!!.contains("dollars")) diningGreen else diningBlue
                            setDrawCircles(false)
                            setDrawValues(false)
                            lineWidth = 4f
                        }

                        val predictionSet = LineDataSet(predictionEntries, "Prediction").apply {
                            color = AndroidColor.GRAY
                            setDrawCircles(false)
                            setDrawValues(false)
                            enableDashedLine(10f, 5f, 0f)
                            lineWidth = 4f
                        }

                        data = LineData(actualSet, predictionSet)

                        val endX = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toFloat()
                        val ll = LimitLine(endX, "End of Term").apply {
                            lineColor = AndroidColor.RED
                            lineWidth = 2f
                            textColor = AndroidColor.RED
                            textSize = 12f
                        }
                        xAxis.addLimitLine(ll)
                        xAxis.axisMaximum = endX

                        val marker = DiningMarkerView(context, R.layout.dining_marker_view)
                        marker.setGraphType(if (cell.type!!.contains("dollars")) 2 else 3)
                        this.marker = marker
                        setDrawMarkers(true)

                        actualSet.isHighlightEnabled = true
                        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                            override fun onValueSelected(e: Entry?, h: Highlight?) {
                                e?.let { entry ->
                                    val cal = Calendar.getInstance().apply {
                                        timeZone = TimeZone.getTimeZone("UTC")
                                        time = startDate
                                        add(Calendar.DAY_OF_YEAR, entry.x.toInt())
                                    }
                                    val formattedDate = SimpleDateFormat("MMM d", Locale.US).apply {
                                        timeZone = TimeZone.getTimeZone("UTC")
                                    }.format(cal.time)
                                    val amount = entry.y
                                    selectedInfo = "$formattedDate → $amount"

                                    if (h != null) {
                                        marker.refreshContent(entry, h)
                                        invalidate()
                                    }
                                }
                            }

                            override fun onNothingSelected() {
                                selectedInfo = null
                            }
                        })

                        description = Description().apply { text = "" }
                        setTouchEnabled(true)
                        setPinchZoom(true)
                        axisRight.isEnabled = false
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)
                        axisLeft.setDrawGridLines(false)
                        axisLeft.axisMinimum = 0f
                        legend.isEnabled = false
                        isDoubleTapToZoomEnabled = false

                        invalidate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (outOfFundsDate != null) {
                    Column(modifier = Modifier.weight(0.4f)) {
                        Text(
                            text = "Out of ${if (cell.type!!.contains("dollars")) "Dollars" else "Swipes"}",
                            fontFamily = GilroyLight,
                            fontSize = 14.sp
                        )
                        Text(
                            text = outOfFundsDate,
                            fontFamily = GilroyBold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        modifier = Modifier.weight(0.6f).fillMaxWidth(),
                        fontFamily = GilroyLight,
                        fontSize = 11.sp,
                        softWrap = true,
                        lineHeight = 13.sp,
                        text = "Based on your current balance and past behavior, we project you'll run out on this date."
                    )
                } else {
                    Column(modifier = Modifier.weight(0.4f)) {
                        val balanceFormatted: String = if (cell.type!!.contains("dollars")) {
                            "$" + String.format(Locale.US, "%.2f", predictedEndBalance)
                        } else {
                            String.format(Locale.US, "%d Swipes", predictedEndBalance?.coerceAtLeast(0f)?.roundToInt())
                        }
                        Text(
                            text = "Extra ${if (cell.type!!.contains("dollars")) "Balance" else "Swipes"}",
                            fontFamily = GilroyLight,
                            fontSize = 14.sp
                        )
                        Text(
                            text = balanceFormatted,
                            fontFamily = GilroyBold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        modifier = Modifier.weight(0.6f).fillMaxWidth(),
                        text = "Based on your current balance and past behavior, we project you'll have this many extra ${if (cell.type!!.contains("dollars")) "dollars" else "swipes"}.",
                        fontFamily = GilroyLight,
                        fontSize = 11.sp,
                        softWrap = true,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}