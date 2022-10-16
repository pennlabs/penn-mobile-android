package com.pennapps.labs.pennmobile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import kotlinx.android.synthetic.main.university_event.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class UniversityEventAdapter(private var events: ArrayList<CalendarEvent>) :
    RecyclerView.Adapter<UniversityEventAdapter.UniversityEventViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.university_event, parent, false)
        mContext = parent.context
        return UniversityEventViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UniversityEventViewHolder, position: Int) {
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
        holder.itemView.event_month.text = event.date
        holder.itemView.event_name_tv.text = name
        holder.itemView.event_name_tv.isSelected = true
        /* if (from == to) {
            holder.itemView.event_day_of_week.text = start
        } else {
            holder.itemView.event_day_of_week.text = "$start - $end"
        }*/

    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class UniversityEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
