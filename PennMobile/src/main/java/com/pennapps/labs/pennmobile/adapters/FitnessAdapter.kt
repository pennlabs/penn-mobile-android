package com.pennapps.labs.pennmobile.adapters

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
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.pennapps.labs.pennmobile.classes.FitnessRoom
import com.pennapps.labs.pennmobile.classes.FitnessRoomUsage
import com.pennapps.labs.pennmobile.classes.RoundedBarChartRenderer
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FitnessAdapter(private val fitnessRooms: List<FitnessRoom>) :
        RecyclerView.Adapter<FitnessAdapter.ViewHolder>() {

    private lateinit var mActivity: Activity
    private lateinit var mContext : Context
    private lateinit var mStudentLife : StudentLife
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainView : ConstraintLayout
        val roomView : TextView
        val statusView : TextView
        val hoursView : TextView
        val imageView : ImageView
        val progressBar : ProgressBar
        val arrowView : ImageView

        private val extraInfoView : LinearLayout
        private val barChart : BarChart

        var extraIsVisible = false
        var hasExtraData = false

        init {
            mainView = view.findViewById(R.id.fitness_list_info_layout)
            roomView = view.findViewById(R.id.item_fitness_name)
            imageView = view.findViewById(R.id.item_fitness_image)
            statusView = view.findViewById(R.id.item_fitness_status)
            hoursView = view.findViewById(R.id.item_fitness_hours)
            arrowView = view.findViewById(R.id.fitness_more_indicator)

            extraInfoView = view.findViewById(R.id.fitness_list_extra_layout)
            progressBar = view.findViewById(R.id.fitness_progress)
            barChart = view.findViewById(R.id.barchart_times)
        }

        fun getExtraData(context: Context, activity: Activity, studentLife : StudentLife,
            room: FitnessRoom
        ) {
            if (hasExtraData) return
            room.roomId?.let { studentLife.getFitnessRoomUsage(it, 3, "week").subscribe(
                { roomUsage ->
                    createBarChart(context, roomUsage)
                    activity.runOnUiThread {
                        hasExtraData = true
                        showExtra()
                    }
                }, {
                    Log.e("Fitness Adapter", "Error loading room usage", it)
                    Toast.makeText(context, "Error loading room", Toast.LENGTH_SHORT).show()
                }
            )}
        }
        private fun createBarChart(context: Context, roomData: FitnessRoomUsage) {
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

            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels);

            val set = BarDataSet(entries, "BarDataSet")

            set.setDrawValues(false)
            set.colors = colors

            val data = BarData(set)
            data.isHighlightEnabled = false;
            data.barWidth = 0.5f // set custom bar width
            val tf = ResourcesCompat.getFont(context, R.font.sf_pro_display_regular)
            barChart.xAxis.typeface = tf

            val leftAxis: YAxis = barChart.axisLeft
            leftAxis.setDrawGridLines(false)
            leftAxis.setDrawAxisLine(false)
            leftAxis.setDrawLabels(false);

            leftAxis.axisMinimum = -0.05f * mxUsage
            leftAxis.axisMaximum = mxUsage

            val rightAxis: YAxis = barChart.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setDrawAxisLine(false)
            rightAxis.setDrawLabels(false);

            val xAxis : XAxis = barChart.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.axisLineWidth = 1f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(true)

            barChart.legend.isEnabled = false;
            barChart.setDrawBorders(false)
            barChart.setFitBars(true)
            barChart.description.isEnabled = false
            barChart.setScaleEnabled(false);

            barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator,
                barChart.viewPortHandler, 50.0f)

            barChart.data = data

            barChart.invalidate() //refresh the bar chart
        }

        fun showExtra() {
            val view = extraInfoView

            if (!extraIsVisible) {
                view.visibility = View.VISIBLE;
                view.alpha = 0.0f;

                view.animate()
                    .alpha(1.0f)

                val rotate = RotateAnimation(
                    0f,
                    90f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate.duration = 200
                rotate.fillAfter = true;
                rotate.interpolator = LinearInterpolator()
                arrowView.startAnimation(rotate)
            } else {
                view.visibility = View.GONE;

                val rotate = RotateAnimation(
                    90f,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                rotate.duration = 10
                rotate.fillAfter = true;
                rotate.interpolator = LinearInterpolator()
                arrowView.startAnimation(rotate)
            }
            extraIsVisible = !extraIsVisible
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fitness_list_item, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance
        return ViewHolder(view)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        // rerotate the image if the extra information panels are open
        if (holder.extraIsVisible) {
            val rotate = RotateAnimation(
                0f,
                90f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 10
            rotate.fillAfter = true;
            rotate.interpolator = LinearInterpolator()
            holder.arrowView.startAnimation(rotate)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = fitnessRooms[position]
        holder.roomView.text = room.roomName

        // check if the room is currently open
        // NOT time zone safe
        val currentTime = LocalTime.now()

        // Sunday -> 0, Monday -> 1, etc.
        val dayOfWeek = ZonedDateTime.now().dayOfWeek.value;

        // the open and close time lists start with monday
        val openTimeString = room.openTimeList?.get((dayOfWeek + 6) % 7)
        val closeTimeString = room.closeTimeList?.get((dayOfWeek + 6) % 7)

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

        // make progress bar invisible
        holder.progressBar.visibility = View.INVISIBLE

        // get image from url
        Glide.with(mContext).load(room.imageURL).into(holder.imageView);

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

    override fun getItemCount() = fitnessRooms.size
}
