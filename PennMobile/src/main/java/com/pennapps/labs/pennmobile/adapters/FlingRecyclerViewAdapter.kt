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
import com.pennapps.labs.pennmobile.databinding.FlingPerformanceItemBinding
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class FlingRecyclerViewAdapter(
    private val context: Context?,
    private val sampleData: List<FlingEvent>,
) : RecyclerView.Adapter<FlingRecyclerViewAdapter.ViewHolder>() {
    private val timeFormatter: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

    override fun getItemCount(): Int = sampleData.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val itemBinding = FlingPerformanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        if (position < itemCount) {
            val flingEvent = sampleData[position]
            val picasso = Picasso.get()
            picasso.isLoggingEnabled = true
            if (holder.flingviewImage != null) picasso.load(flingEvent.imageUrl).into(holder.flingviewImage)
            holder.flingviewName?.text = flingEvent.name
            holder.flingviewDescription?.text = flingEvent.description
            val startTime = timeFormatter.parseDateTime(flingEvent.startTime)
            val endTime = timeFormatter.parseDateTime(flingEvent.endTime)
            val dtfStart = DateTimeFormat.forPattern("h:mm")
            val dtfEnd = DateTimeFormat.forPattern("h:mm a")
            holder.flingviewTime?.text =
                String.format(
                    context?.resources?.getString(R.string.fling_event_time).toString(),
                    dtfStart.print(startTime),
                    dtfEnd.print(endTime),
                )
        }
    }

    class ViewHolder(
        itemBinding: FlingPerformanceItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        internal var flingviewImage: ImageView? = itemBinding.flingviewImage
        internal var flingviewName: TextView? = itemBinding.flingviewName
        internal var flingviewDescription: TextView? = itemBinding.flingviewDescription
        internal var flingviewTime: TextView? = itemBinding.flingviewTime
    }
}
