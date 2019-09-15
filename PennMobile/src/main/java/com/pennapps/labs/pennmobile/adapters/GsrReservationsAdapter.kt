package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_reservation.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GsrReservationsAdapter(private val reservations: List<GSRReservation>)// get reservations data from fragment
    : RecyclerView.Adapter<GsrReservationsAdapter.GsrReservationViewHolder>() {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GsrReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_reservation, parent, false)
        mContext = parent.context
        return GsrReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GsrReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        // get the data from GsrReservation class
        val roomName = reservation.name

        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val from = formatter.parseDateTime(reservation.fromDate)
        val to = formatter.parseDateTime(reservation.toDate)
        val day = from.toString("EEEE, MMMM d")
        val fromHour = from.toString("h:mm a")
        val toHour = to.toString("h:mm a")

        val imageUrl = reservation.info?.get("thumbnail")

        // set image
        Picasso.get().load(imageUrl).fit().centerCrop().into(holder.itemView.gsr_reservation_iv)

        // update ViewHolder
        holder.itemView.gsr_reservation_location_tv.text = roomName
        holder.itemView.gsr_reservation_date_tv.text = day + "\n" + fromHour + "-" + toHour
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class GsrReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
    }
}
