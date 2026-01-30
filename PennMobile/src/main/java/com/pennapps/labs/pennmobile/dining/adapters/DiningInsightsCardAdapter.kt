package com.pennapps.labs.pennmobile.dining.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.databinding.DiningBalancesCardBinding
import com.pennapps.labs.pennmobile.databinding.DiningPredictionsCardBinding
import com.pennapps.labs.pennmobile.databinding.DiningSpentCardBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningInsightCell
import com.pennapps.labs.pennmobile.dining.classes.DiningMarkerView
import com.pennapps.labs.pennmobile.dining.viewholders.DiningBalancesCardHolder
import com.pennapps.labs.pennmobile.dining.viewholders.DiningPredictionsHolder
import com.pennapps.labs.pennmobile.dining.viewholders.DiningSpentHolder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class DiningInsightsCardAdapter(
    private var cells: ArrayList<DiningInsightCell>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    companion object {
        // Types of Home Cells
        private const val NOT_SUPPORTED = -1
        private const val DINING_BALANCE = 0
        private const val DINING_DOLLARS_SPENT = 1
        private const val DINING_DOLLARS_PREDICTIONS = 2
        private const val DINING_SWIPES_PREDICTIONS = 3

        const val START_DAY_OF_SEMESTER = "2026-01-14"
        private const val DAYS_IN_SEMESTER = 117f
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity

        return when (viewType) {
            DINING_BALANCE -> {
                val itemBinding = DiningBalancesCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                DiningBalancesCardHolder(itemBinding)
            }

            DINING_DOLLARS_SPENT -> {
                val itemBinding = DiningSpentCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                DiningSpentHolder(itemBinding)
            }

            DINING_DOLLARS_PREDICTIONS -> {
                val itemBinding = DiningPredictionsCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                DiningPredictionsHolder(itemBinding)
            }

            DINING_SWIPES_PREDICTIONS -> {
                val itemBinding = DiningPredictionsCardBinding.inflate(LayoutInflater.from(mContext), parent, false)
                DiningPredictionsHolder(itemBinding)
            }

            NOT_SUPPORTED -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }

            else -> {
                ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.empty_view, parent, false))
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val cell = cells[position]
        when (cell.type) {
            "dining_balance" -> bindDiningBalanceCells(holder as DiningBalancesCardHolder, cell)
            "dining_dollars_spent" -> bindDollarsSpentReservationsCell(holder as DiningSpentHolder, cell)
            "dining_dollars_predictions" -> bindDiningDollarsPredictions(holder as DiningPredictionsHolder, cell)
            "dining_swipes_predictions" -> bindDiningSwipesPredictions(holder as DiningPredictionsHolder, cell)
            else -> Log.i("HomeAdapter", "Unsupported type of data at position $position")
        }
    }

    override fun getItemCount(): Int = cells.size

    inner class ViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }

    override fun getItemViewType(position: Int): Int {
        val cell = cells[position]
        return when (cell.type) {
            "dining_balance" -> DINING_BALANCE
            "dining_dollars_predictions" -> DINING_DOLLARS_PREDICTIONS
            "dining_swipes_predictions" -> DINING_SWIPES_PREDICTIONS
            "dining_dollars_spent" -> DINING_DOLLARS_SPENT
            else -> NOT_SUPPORTED
        }
    }

    private fun filterPastBalances(values: List<Entry>): List<Entry> {
        if (values.size <= 2) {
            return values
        }
        return values.filterIndexed { index, currBalance ->
            if (index == 0 || index == values.size - 1) {
                return@filterIndexed true
            }
            val prevBalance = values[index - 1].y
            val nextBalance = values[index + 1].y

            return@filterIndexed currBalance.y != 0f ||
                prevBalance <= 0f ||
                nextBalance <= 0f
        }
    }

    private fun switchBalances(values: List<Entry>): List<Entry> {
        var max = 0f

        for (v in values) {
            if (v.y > max) {
                max = v.y
            }
        }

        Log.i("swipes", max.toString())

        for (v in values) {
            v.y = max - v.y
            Log.i("new swipes", v.y.toString())
        }

        return values
    }

    private fun bindDollarsSpentReservationsCell(
        holder: DiningSpentHolder,
        cell: DiningInsightCell,
    ) {
        // Populate dining dollars spent card
    }

    private fun bindDiningDollarsPredictions(
        holder: DiningPredictionsHolder,
        cell: DiningInsightCell,
    ) {
        val tvPredictionsTitle = holder.predictionsTitle
        tvPredictionsTitle.text = mContext.getString(R.string.dining_dollars_predictions)
        bindPredictions(holder, cell, DINING_DOLLARS_PREDICTIONS)
    }

    private fun bindDiningSwipesPredictions(
        holder: DiningPredictionsHolder,
        cell: DiningInsightCell,
    ) {
        val tvPredictionsTitle = holder.predictionsTitle
        tvPredictionsTitle.text = mContext.getString(R.string.dining_swipes_predictions)
        bindPredictions(holder, cell, DINING_SWIPES_PREDICTIONS)
    }

    private fun bindPredictions(
        holder: DiningPredictionsHolder,
        cell: DiningInsightCell,
        typeId: Int,
    ) {
        // Populate dining dollars predictions card
        if (cell.diningBalancesList == null) {
            return
        }
        val dates = ArrayList<String>()
        val amounts = ArrayList<Float>()
        val currentBalance = cell.diningBalances
        val diningBalances = cell.diningBalancesList?.diningBalancesList
        diningBalances?.forEach {
            it.date?.let { it1 -> dates.add(it1) }
            if (typeId == DINING_DOLLARS_PREDICTIONS) {
                it.diningDollars?.let { it1 -> amounts.add(it1.toFloat()) }
            } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                it.regularVisits?.let { it1 -> amounts.add(it1.toFloat()) }
            }
        }
        if (amounts.isNotEmpty()) {
            if (amounts.last() == 0f) {
                if (typeId == DINING_DOLLARS_PREDICTIONS) {
                    currentBalance?.diningDollars?.let { it1 ->
                        amounts[amounts.lastIndex] = it1.toFloat()
                    }
                } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                    currentBalance?.regularVisits?.let { it1 ->
                        amounts[amounts.lastIndex] = it1.toFloat()
                    }
                }
            }
        }

        val tf = mContext.resources.getFont(R.font.gilroy_light)
        val predictionChart = holder.diningPredictionsGraph
        predictionChart.description = null
        predictionChart.legend.isEnabled = false
        predictionChart.setDrawBorders(false)
        predictionChart.axisRight.setDrawGridLines(false)
        predictionChart.axisLeft.setDrawGridLines(false)
        predictionChart.xAxis.setDrawGridLines(false)
        val endOfTermLine = LimitLine(DAYS_IN_SEMESTER, "End of Term")
        endOfTermLine.lineColor = mContext.getColor(R.color.red)
        endOfTermLine.lineWidth = 3f
        endOfTermLine.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        endOfTermLine.textSize = 10f
        endOfTermLine.textColor = mContext.getColor(R.color.red)
        endOfTermLine.typeface = tf
        val xAxis: XAxis = predictionChart.xAxis
        xAxis.typeface = tf
        val position = XAxisPosition.BOTTOM
        xAxis.position = position
        xAxis.axisMaximum = DAYS_IN_SEMESTER
        xAxis.axisMinimum = 0f
        xAxis.setLabelCount(6, true)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 7f
        xAxis.labelRotationAngle = 315f
        xAxis.valueFormatter = ClaimsXAxisValueFormatter(dates)
        predictionChart.axisRight.isEnabled = false
        val yAxis: YAxis = predictionChart.axisLeft
        yAxis.typeface = tf
        yAxis.removeAllLimitLines()
        if (amounts.isEmpty()) {
            yAxis.axisMaximum = 0f
        } else {
            yAxis.axisMaximum = amounts.max()
        }

        yAxis.axisMinimum = 0f
        yAxis.setDrawZeroLine(false)
        yAxis.setDrawLimitLinesBehindData(false)
        setData(amounts, predictionChart, holder, typeId)
        xAxis.removeAllLimitLines()
        xAxis.addLimitLine(endOfTermLine)

        // Enable touch gestures and marker
        predictionChart.setTouchEnabled(true)
        predictionChart.setPinchZoom(false)
        predictionChart.setScaleEnabled(false)

        val markerView = DiningMarkerView(mContext, R.layout.dining_marker_view)
        predictionChart.marker = markerView

        // Set an event listener to display marker content
        predictionChart.setOnChartValueSelectedListener(
            object :
                OnChartValueSelectedListener {
                override fun onValueSelected(
                    e: Entry?,
                    h: Highlight?,
                ) {
                    if (e != null) {
                        if (h != null) {
                            markerView.setGraphType(typeId)
                            markerView.refreshContent(e, h)
                        }
                        predictionChart.invalidate()
                    }
                }

                override fun onNothingSelected() {
                    // Hide the marker when nothing is selected
                    predictionChart.marker = null
                }
            },
        )
        // Don't think this is necessary, add just in case
        predictionChart.invalidate()
    }

    private fun bindDiningBalanceCells(
        holder: DiningBalancesCardHolder,
        cell: DiningInsightCell,
    ) {
        val diningBalances = cell.diningBalances
        val diningDollars = "$" + (diningBalances?.diningDollars ?: "0.00")
        val swipes = diningBalances?.regularVisits ?: 0
        val guestSwipes = diningBalances?.guestVisits ?: 0
        val tvDiningDollarsAmount = holder.diningDollarsAmount
        tvDiningDollarsAmount.text = diningDollars
        val tvRegularSwipesAmount = holder.swipesAmount
        tvRegularSwipesAmount.text = swipes.toString()
        val tvGuestSwipesAmount = holder.guestSwipesAmount
        tvGuestSwipesAmount.text = guestSwipes.toString()
    }

    private fun getPredictionSlope(values: List<Entry>): Float =
        if (values.size <= 1) {
            0f
        } else {
            (values[values.size - 1].y - values[0].y) / (values.size - 1)
        }

    private fun setData(
        amounts: List<Float>,
        diningDollarsGraph: LineChart,
        holder: DiningPredictionsHolder,
        typeId: Int,
    ) {
        val values: ArrayList<Entry> = ArrayList()
        amounts.forEachIndexed { index, amount ->
            values.add(Entry(index.toFloat(), amount))
        }
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedCurrentDate = current.format(formatter)

        var filteredValues = filterPastBalances(values)
        if (formattedCurrentDate.equals("2025-04-01")) { // April Fool's
            filteredValues = switchBalances(filteredValues)
        }
        if (filteredValues.isEmpty()) {
            return
        }
        val predictionValues: ArrayList<Entry> = ArrayList()
        val slope = getPredictionSlope(values)
        val b = if (filteredValues.isNotEmpty()) filteredValues[0].y else 0f
        for (i in values.size - 1..DAYS_IN_SEMESTER.toInt()) {
            predictionValues.add(Entry(i.toFloat(), slope * i + b))
        }
        val actualValues: LineDataSet
        if (diningDollarsGraph.data != null &&
            diningDollarsGraph.data.dataSetCount > 0
        ) {
            actualValues = diningDollarsGraph.data.getDataSetByIndex(0) as LineDataSet
            actualValues.values = filteredValues
            diningDollarsGraph.data.notifyDataChanged()
            diningDollarsGraph.notifyDataSetChanged()
        } else {
            actualValues = LineDataSet(filteredValues, "")
            actualValues.setDrawValues(false)
            actualValues.setDrawCircles(false)
            actualValues.setDrawFilled(false)
            actualValues.setDrawHighlightIndicators(false)
            if (typeId == DINING_DOLLARS_PREDICTIONS) {
                actualValues.color = mContext.getColor(R.color.diningGreen)
            } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                actualValues.color = mContext.getColor(R.color.diningBlue)
            }
            actualValues.lineWidth = 4f // line size
            val predictionSet = LineDataSet(predictionValues, "Predictions")
            predictionSet.setDrawValues(false)
            predictionSet.setDrawCircles(false)
            predictionSet.setDrawFilled(false)
            predictionSet.color = mContext.getColor(R.color.gray)
            predictionSet.lineWidth = 4f
            predictionSet.enableDashedLine(10f, 10f, 0f)
            predictionSet.setDrawHighlightIndicators(false)
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(actualValues)
            dataSets.add(predictionSet)
            val data = LineData(dataSets)
            diningDollarsGraph.data = data
            diningDollarsGraph.animateX(1750, Easing.Linear)
            val tvExpiredOrExtra = holder.extraAmount
            val tvExtra = holder.extra
            val tvExtraNote = holder.extraNote
            if (filteredValues[filteredValues.size - 1].y <= 0) {
                val expiredDate = getExpired(filteredValues as ArrayList)
                tvExpiredOrExtra.text = expiredDate
                if (typeId == DINING_DOLLARS_PREDICTIONS) {
                    tvExtra.text = mContext.getString(R.string.out_of_dining_dollars)
                    tvExtraNote.text = mContext.getString(R.string.out_of_dining_dollars_message)
                } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                    tvExtra.text = mContext.getString(R.string.out_of_dining_swipes)
                    tvExtraNote.text = mContext.getString(R.string.out_of_dining_swipes_message)
                }
            } else {
                if (predictionValues.size != 0 && predictionValues[predictionValues.size - 1].y < 0) {
                    val predictedExpiredDate = getPredictedExpired(values.size, predictionValues)
                    tvExpiredOrExtra.text = predictedExpiredDate
                    if (typeId == DINING_DOLLARS_PREDICTIONS) {
                        tvExtra.text = mContext.getString(R.string.out_of_dining_dollars)
                        tvExtraNote.text = mContext.getString(R.string.out_of_dining_dollars_prediction_message)
                    } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                        tvExtra.text = mContext.getString(R.string.out_of_dining_swipes)
                        tvExtraNote.text = mContext.getString(R.string.out_of_dining_swipes_prediction_message)
                    }
                } else {
                    var extraAmount = filteredValues[filteredValues.size - 1].y
                    if (predictionValues.size != 0) {
                        extraAmount = predictionValues[predictionValues.size - 1].y
                    }
                    tvExtra.text = mContext.getString(R.string.extra_balance)
                    if (typeId == DINING_DOLLARS_PREDICTIONS) {
                        tvExtraNote.text = mContext.getString(R.string.extra_dining_dollars_message)
                        tvExpiredOrExtra.text =
                            buildString {
                                append(mContext.getString(R.string.money))
                                append(String.format("%.2f", extraAmount))
                            }
                    } else if (typeId == DINING_SWIPES_PREDICTIONS) {
                        tvExtraNote.text = mContext.getString(R.string.extra_dining_swipes_message)
                        tvExpiredOrExtra.text =
                            buildString {
                                append(extraAmount.toInt().toString())
                                append(" ")
                                append(mContext.getString(R.string.dining_swipes))
                            }
                    }
                }
            }
        }
    }

    private fun getExpired(actualValues: ArrayList<Entry>): String {
        var i = 0
        run breaking@{
            actualValues.forEachIndexed { index, value ->
                if (value.y <= 0) {
                    i = index
                    return@breaking
                }
            }
        }
        return Utils.addDaysToDateMMMdd(START_DAY_OF_SEMESTER, i)
    }

    private fun getPredictedExpired(
        actualValuesSize: Int,
        predictedValues: ArrayList<Entry>,
    ): String {
        var i = 0
        run breaking@{
            predictedValues.forEachIndexed { index, predictedValue ->
                if (predictedValue.y <= 0) {
                    i = index
                    return@breaking
                }
            }
        }
        return Utils.addDaysToDateMMMdd(START_DAY_OF_SEMESTER, actualValuesSize + i)
    }

    class ClaimsXAxisValueFormatter(
        var datesList: List<String>,
    ) : ValueFormatter() {
        override fun getAxisLabel(
            value: Float,
            axis: AxisBase,
        ): String {
            val daysFromStart = value.roundToInt()
            val sdf = SimpleDateFormat("MMM. dd")
            val date = Utils.addDaysToDate(START_DAY_OF_SEMESTER, daysFromStart)
            return sdf.format(
                Date(
                    Utils.getDateInMilliSeconds(
                        date,
                        "yyyy-MM-dd",
                    ),
                ),
            )
        }
    }

    class Utils {
        companion object {
            fun addDaysToDate(
                startDate: String,
                daysFromStart: Int,
            ): String {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val cal = Calendar.getInstance()
                try {
                    cal.time = sdf.parse(startDate) as Date
                } catch (error: ParseException) {
                    Log.e("DiningInsightsAdapter", "Error getting date", error)
                }
                cal.add(Calendar.DAY_OF_MONTH, daysFromStart)
                return sdf.format(cal.time)
            }

            fun addDaysToDateMMMdd(
                startDate: String,
                daysFromStart: Int,
            ): String {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val mmmDD = SimpleDateFormat("MMM. dd")
                val cal = Calendar.getInstance()
                try {
                    cal.time = sdf.parse(startDate) as Date
                } catch (error: ParseException) {
                    Log.e("DiningInsightsAdapter", "Error getting date", error)
                }
                cal.add(Calendar.DAY_OF_MONTH, daysFromStart)
                return mmmDD.format(cal.time)
            }

            fun getDateInMilliSeconds(
                givenDateString: String?,
                format: String,
            ): Long {
                val sdf = SimpleDateFormat(format, Locale.US)
                var timeInMilliseconds: Long = 1
                try {
                    val mDate = givenDateString?.let { sdf.parse(it) }
                    if (mDate != null) {
                        timeInMilliseconds = mDate.time
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return timeInMilliseconds
            }
        }
    }
}
