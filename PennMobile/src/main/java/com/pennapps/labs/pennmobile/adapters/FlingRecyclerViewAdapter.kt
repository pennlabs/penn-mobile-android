package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.FlingPerformanceViewHolder
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class FlingRecyclerViewAdapter(private val context: Context, private val sampleData: List<FlingEvent>) : RecyclerView.Adapter<FlingPerformanceViewHolder>() {

    private val timeFormatter: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override fun getItemCount(): Int {
        return sampleData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlingPerformanceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fling_performance_item, parent, false)
        return FlingPerformanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlingPerformanceViewHolder, position: Int) {
        if (position < itemCount) {
            val flingEvent = sampleData[position]
            Picasso.get().load(flingEvent.imageUrl).into(holder.flingview_image)
            holder.flingview_name.text = flingEvent.name
            holder.flingview_description.text = flingEvent.description
            val startTime = timeFormatter.parseDateTime(flingEvent.startTime)
            val endTime = timeFormatter.parseDateTime(flingEvent.endTime)
            val dtfStart = DateTimeFormat.forPattern("h:mm")
            val dtfEnd = DateTimeFormat.forPattern("h:mm a")
            holder.flingview_time.text = String.format(context.resources.getString(R.string.fling_event_time), dtfStart.print(startTime), dtfEnd.print(endTime))
        }
    }


}