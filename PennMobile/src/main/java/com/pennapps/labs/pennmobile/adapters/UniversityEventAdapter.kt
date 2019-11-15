package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.CalendarEvent
import kotlinx.android.synthetic.main.university_event.view.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class UniversityEventAdapter(private var events: ArrayList<CalendarEvent>)
    : RecyclerView.Adapter<UniversityEventAdapter.UniversityEventViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.university_event, parent, false)
        mContext = parent.context
        return UniversityEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversityEventViewHolder, position: Int) {
        val event = events[position]

        val name = event.name

        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val from = formatter.parseDateTime(event.start)
        val to = formatter.parseDateTime(event.end)
        val start = from.toString("EEE, MMM d")
        val end = to.toString("EEE, MMM d")

        holder.itemView.event_name_tv.text = name
        if (start.equals(end)) {
            holder.itemView.event_date_tv.text = event.start
        } else {
            holder.itemView.event_date_tv.text = event.start + " - " + event.end
        }

    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class UniversityEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
