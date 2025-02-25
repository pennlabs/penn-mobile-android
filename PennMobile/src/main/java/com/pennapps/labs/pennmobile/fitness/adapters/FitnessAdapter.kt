package com.pennapps.labs.pennmobile.fitness.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.fitness.classes.FitnessAdapterDataModel
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoom
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoomUsage
import com.pennapps.labs.pennmobile.fitness.classes.RoundedBarChartRenderer
import rx.schedulers.Schedulers
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FitnessAdapter(
    private val isFavorite: Boolean,
    private val dataModel: FitnessAdapterDataModel,
) : RecyclerView.Adapter<FitnessAdapter.ViewHolder>() {
    private lateinit var mActivity: Activity
    private lateinit var mContext: Context
    private lateinit var mStudentLife: StudentLife

    class ViewHolder(
        val view: View,
    ) : RecyclerView.ViewHolder(view) {
        val mainView: ConstraintLayout = view.findViewById(R.id.fitness_list_info_layout)
        val roomView: TextView = view.findViewById(R.id.item_fitness_name)
        val statusView: TextView = view.findViewById(R.id.item_fitness_status)
        val hoursView: TextView = view.findViewById(R.id.item_fitness_hours)
        val imageView: ImageView = view.findViewById(R.id.item_fitness_image)
        val progressBar: ProgressBar = view.findViewById(R.id.fitness_progress)
        val arrowView: ImageView = view.findViewById(R.id.fitness_more_indicator)

        val timeCapacityView: TextView = view.findViewById(R.id.timeCapacity)
        val lastUpdatedView: TextView = view.findViewById(R.id.item_pottruck_last_updated)
        val capacityViewCircle: com.google.android.material.progressindicator.CircularProgressIndicator =
            view.findViewById(R.id.item_pottruck_capacity_circle)
        val capacityView: TextView = view.findViewById(R.id.item_pottruck_capacity)

        private val extraInfoView: LinearLayout = view.findViewById(R.id.fitness_list_extra_layout)
        private val barChart: BarChart = view.findViewById(R.id.barchart_times)

        var extraIsVisible = false
        var hasExtraData = false

        fun getExtraData(
            context: Context,
            activity: Activity,
            studentLife: StudentLife,
            room: FitnessRoom,
        ) {
            if (hasExtraData) return
            room.roomId?.let {
                try {
                    studentLife
                        .getFitnessRoomUsage(it, 3, "week")
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            { roomUsage ->
                                createBarChart(context, roomUsage!!)
                                activity.runOnUiThread {
                                    hasExtraData = true
                                    showExtra()
                                }
                            },
                            {
                                Log.e("Fitness Adapter", "Error loading room usage", it)
                                Toast.makeText(context, "Error loading room", Toast.LENGTH_SHORT).show()
                            },
                        )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun createBarChart(
            context: Context,
            roomData: FitnessRoomUsage,
        ) {
            val entries: MutableList<BarEntry> = ArrayList()
            val labels: MutableList<String> = ArrayList()
            val colors: MutableList<Int> = ArrayList()

            val blue = Color.parseColor("#A3CBF2")
            val darkBlue = Color.parseColor("#1280F0")

            val timeData = roomData.roomUsage
            val curHour = LocalDateTime.now().hour
            var mxUsage = 0f
            // assume that we start at 6am and end at 11 pm
            for (i in 6..23) {
                val v = if (timeData == null) 0f else timeData[i.toString()] ?: 0f
                mxUsage = maxOf(mxUsage, v)
                entries.add(BarEntry((i - 6).toFloat(), v))
                colors.add(if (i == curHour) darkBlue else blue)
                labels.add(if (i > 12) "${i - 12}pm" else "${i}am")
            }

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            val set = BarDataSet(entries, "BarDataSet")

            set.setDrawValues(false)
            set.colors = colors

            val data = BarData(set)
            data.isHighlightEnabled = false
            data.barWidth = 0.5f // set custom bar width
            val tf = ResourcesCompat.getFont(context, R.font.sf_pro_display_regular)
            barChart.xAxis.typeface = tf

            val leftAxis: YAxis = barChart.axisLeft
            leftAxis.setDrawGridLines(false)
            leftAxis.setDrawAxisLine(false)
            leftAxis.setDrawLabels(false)

            leftAxis.axisMinimum = -0.05f * mxUsage
            leftAxis.axisMaximum = mxUsage

            val rightAxis: YAxis = barChart.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setDrawAxisLine(false)
            rightAxis.setDrawLabels(false)

            val xAxis: XAxis = barChart.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.axisLineWidth = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(true)

            barChart.legend.isEnabled = false
            barChart.setDrawBorders(false)
            barChart.setFitBars(true)
            barChart.description.isEnabled = false
            barChart.setScaleEnabled(false)

            barChart.renderer =
                RoundedBarChartRenderer(
                    barChart,
                    barChart.animator,
                    barChart.viewPortHandler,
                    50.0f,
                )

            barChart.data = data

            barChart.invalidate() // refresh the bar chart
        }

        fun showExtra() {
            val view = extraInfoView

            if (!extraIsVisible) {
                view.visibility = View.VISIBLE
                view.alpha = 0.0f

                view
                    .animate()
                    .alpha(1.0f)

                val rotate =
                    RotateAnimation(
                        0f,
                        90f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                    )
                rotate.duration = 200
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()
                arrowView.startAnimation(rotate)
            } else {
                view.visibility = View.GONE

                val rotate =
                    RotateAnimation(
                        90f,
                        0f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                    )
                rotate.duration = 10
                rotate.fillAfter = true
                rotate.interpolator = LinearInterpolator()
                arrowView.startAnimation(rotate)
            }
            extraIsVisible = !extraIsVisible
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fitness_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        // re-rotate the image if the extra information panels are open
        if (holder.extraIsVisible) {
            val rotate =
                RotateAnimation(
                    0f,
                    90f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                )
            rotate.duration = 10
            rotate.fillAfter = true
            rotate.interpolator = LinearInterpolator()
            holder.arrowView.startAnimation(rotate)
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val room = dataModel.getRoom(isFavorite, position)
        holder.roomView.text = room.roomName

        // check if the room is currently open
        // NOT time zone safe
        val currentTime = LocalTime.now()

        // dayOfWeek gives Sunday -> 0, Monday -> 1, etc.
        // but we want 0 -> monday
        val dayOfWeek = (ZonedDateTime.now().dayOfWeek.value + 6) % 7

        // the open and close time lists start with monday
        val openTimeString = room.openTimeList?.get(dayOfWeek)
        val closeTimeString = room.closeTimeList?.get(dayOfWeek)

        val openTime = LocalTime.parse(openTimeString)
        val closeTime = LocalTime.parse(closeTimeString)

        val isOpen = currentTime.isAfter(openTime) and currentTime.isBefore(closeTime)
        if (isOpen) {
            holder.statusView.setText(R.string.fitness_room_open)
            holder.statusView.background = ContextCompat.getDrawable(mContext, R.drawable.label_green)
        } else {
            holder.statusView.setText(R.string.fitness_room_closed)
            holder.statusView.background = ContextCompat.getDrawable(mContext, R.drawable.label_red)
        }

        // format and assign the times
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val hours = "${openTime.format(timeFormatter)} - ${closeTime.format(timeFormatter)}"
        holder.hoursView.text = hours

        // format and assign the times for the rest of the week
        // definitely not the best way to do this, but I am lazy

        val mfIndex = if (dayOfWeek < 5) dayOfWeek else 0
        val mfOpenTime = LocalTime.parse(room.openTimeList?.get(mfIndex))
        val mfCloseTime = LocalTime.parse(room.closeTimeList?.get(mfIndex))
        val mfHours = "${mfOpenTime.format(timeFormatter)} - ${mfCloseTime.format(timeFormatter)}"

        val saturdayIndex = 5
        val saturdayOpenTime = LocalTime.parse(room.openTimeList?.get(saturdayIndex))
        val saturdayCloseTime = LocalTime.parse(room.closeTimeList?.get(saturdayIndex))
        val saturdayHours = "${saturdayOpenTime.format(timeFormatter)} - ${saturdayCloseTime.format(timeFormatter)}"

        val sundayIndex = 6
        val sundayOpenTime = LocalTime.parse(room.openTimeList?.get(sundayIndex))
        val sundayCloseTime = LocalTime.parse(room.closeTimeList?.get(sundayIndex))
        val sundayHours = "${sundayOpenTime.format(timeFormatter)} - ${sundayCloseTime.format(timeFormatter)}"

        (holder.view.findViewById(R.id.fitness_sunday_time) as TextView).text = sundayHours
        (holder.view.findViewById(R.id.fitness_mf_time) as TextView).text = mfHours
        (holder.view.findViewById(R.id.fitness_sat_time) as TextView).text = saturdayHours

        val blue = Color.parseColor("#1280F0")

        if (dayOfWeek < 5) {
            (holder.view.findViewById(R.id.fitness_mf) as TextView).setTextColor(blue)
            (holder.view.findViewById(R.id.fitness_mf_time) as TextView).setTextColor(blue)
        } else if (dayOfWeek == 5) {
            (holder.view.findViewById(R.id.fitness_sat) as TextView).setTextColor(blue)
            (holder.view.findViewById(R.id.fitness_sat_time) as TextView).setTextColor(blue)
        } else {
            (holder.view.findViewById(R.id.fitness_sunday) as TextView).setTextColor(blue)
            (holder.view.findViewById(R.id.fitness_sunday_time) as TextView).setTextColor(blue)
        }

        // make progress bar invisible
        holder.progressBar.visibility = View.INVISIBLE

        // get image from url
        Glide.with(mContext).load(room.imageURL).into(holder.imageView)

        val busyness: String

        // update the capacity
        if (room.capacity == null) {
            busyness = "N/A"
            holder.capacityView.text = "N/A"
            holder.capacityViewCircle.progress = 0
        } else {
            val capacityInt = room.capacity!!.toInt()
            val capacity = "$capacityInt%"

            busyness =
                if (capacityInt == 0) {
                    "Empty"
                } else if (capacityInt < 10) {
                    "Not very busy"
                } else if (capacityInt < 30) {
                    "Slightly busy"
                } else if (capacityInt < 60) {
                    "Pretty busy"
                } else if (capacityInt < 90) {
                    "Extremely busy"
                } else {
                    "Packed"
                }

            holder.capacityViewCircle.progress = capacityInt
            holder.capacityView.text = capacity
        }

        // update the time and capacity
        var curHour = currentTime.hour

        val amPm = if (curHour >= 12) "PM" else "AM"
        if (curHour > 12) curHour -= 12
        if (curHour == 0) curHour += 12

        val timeCap = "<a><font color = #1280F0>$curHour $amPm:</a> $busyness"
        holder.timeCapacityView.text = HtmlCompat.fromHtml(timeCap, HtmlCompat.FROM_HTML_MODE_COMPACT)

        // update the time for last updated
        val lastUpdateTime = ZonedDateTime.parse(room.lastUpdated)
        val duration = Duration.between(lastUpdateTime, ZonedDateTime.now())

        val updHours = duration.toHours()

        val lastUpd =
            if (updHours > 2L) {
                "Updated $updHours hours ago"
            } else if (updHours == 1L) {
                "Updated $updHours hour ago"
            } else {
                val updMinutes = duration.toMinutes()
                "Updated $updMinutes minutes ago"
            }

        holder.lastUpdatedView.text = lastUpd

        holder.mainView.bringToFront()
        // garbage code starts here ------------------------------------

        holder.mainView.setOnClickListener {
            if (holder.hasExtraData) {
                holder.showExtra()
            } else {
                holder.getExtraData(mContext, mActivity, mStudentLife, room)
            }
        }
    }

    override fun getItemCount() = dataModel.getNumber(isFavorite)
}
