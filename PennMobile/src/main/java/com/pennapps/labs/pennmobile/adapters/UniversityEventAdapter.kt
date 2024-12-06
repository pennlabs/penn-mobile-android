package com.pennapps.labs.pennmobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import com.pennapps.labs.pennmobile.databinding.UniversityEventBinding

class UniversityEventAdapter(
    private var events: ArrayList<CalendarEvent>,
) : RecyclerView.Adapter<UniversityEventAdapter.UniversityEventViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UniversityEventViewHolder {
        mContext = parent.context
        val itemBinding = UniversityEventBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return UniversityEventViewHolder(itemBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: UniversityEventViewHolder,
        position: Int,
    ) {
        val event = events[position]

        val name = event.name
        Log.i("EventAdapter", "Name $name Date $event.date")
        // val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        // val from = formatter.parseDateTime(event.start)
        // val to = formatter.parseDateTime(event.end)
        // val dayOfMonth = from.toString("d")
        // val month = from.toString("MMM")
        // val start = from.toString("EEEE")
        // val end = to.toString("EEEE")

        // holder.itemView.event_day.text = dayOfMonth
        // holder.itemView.event_month.text = month
        holder.eventMonth.text = event.date
        holder.eventName.text = name
        holder.eventName.isSelected = true
        /* if (from == to) {
            holder.itemView.event_day_of_week.text = start
        } else {
            holder.itemView.event_day_of_week.text = "$start - $end"
        }*/
    }

    override fun getItemCount(): Int = events.size

    inner class UniversityEventViewHolder(
        itemBinding: UniversityEventBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val eventMonth: TextView = itemBinding.eventMonth
        val eventName: TextView = itemBinding.eventNameTv
    }
}
