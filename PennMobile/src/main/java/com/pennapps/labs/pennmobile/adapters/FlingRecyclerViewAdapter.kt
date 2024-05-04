package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fling_performance_item.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class FlingRecyclerViewAdapter(private val context: Context?, private val sampleData: List<FlingEvent>) : RecyclerView.Adapter<FlingRecyclerViewAdapter.ViewHolder>() {
    private val timeFormatter: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override fun getItemCount(): Int {
        return sampleData.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fling_performance_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        if (position < itemCount) {
            val flingEvent = sampleData[position]
            val picasso = Picasso.get()
            picasso.isLoggingEnabled = true
            if (holder.flingview_image != null) picasso.load(flingEvent.imageUrl).into(holder.flingview_image)
            holder.flingview_name?.text = flingEvent.name
            holder.flingview_description?.text = flingEvent.description
            val startTime = timeFormatter.parseDateTime(flingEvent.startTime)
            val endTime = timeFormatter.parseDateTime(flingEvent.endTime)
            val dtfStart = DateTimeFormat.forPattern("h:mm")
            val dtfEnd = DateTimeFormat.forPattern("h:mm a")
            holder.flingview_time?.text =
                String.format(
                    context?.resources?.getString(R.string.fling_event_time).toString(),
                    dtfStart.print(startTime),
                    dtfEnd.print(endTime),
                )
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var flingview_image: ImageView? = view.flingview_image
        internal var flingview_name: TextView? = view.flingview_name
        internal var flingview_description: TextView? = view.flingview_description
        internal var flingview_time: TextView? = view.flingview_time
    }
}
