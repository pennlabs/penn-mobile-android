package com.pennapps.labs.pennmobile.gsr.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.GsrReservationBinding
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GsrReservationsAdapter(
    private var reservations: ArrayList<GSRReservation>,
    private val onCancelRequested: (GSRReservation, Int) -> Unit,
) : RecyclerView.Adapter<GsrReservationsAdapter.GsrReservationViewHolder>() {
    private lateinit var mContext: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GsrReservationViewHolder {
        mContext = parent.context
        val itemBinding = GsrReservationBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return GsrReservationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: GsrReservationViewHolder,
        position: Int,
    ) {
        val reservation = reservations[position]

        val roomName = reservation.name

        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val from = formatter.parseDateTime(reservation.fromDate)
        val to = formatter.parseDateTime(reservation.toDate)
        val day = from.toString("EEEE, MMMM d")
        val fromHour = from.toString("h:mm a")
        val toHour = to.toString("h:mm a")

        // huntsman reservation responses don't have an image url so we set it here
        val imageUrl = reservation.info?.get("thumbnail") ?: "https://s3.us-east-2.amazonaws.com/labs.api/dining/MBA+Cafe.jpg"
        Picasso
            .get()
            .load(imageUrl)
            .fit()
            .centerCrop()
            .into(holder.gsrReservationIv)

        holder.gsrReservationLocationTv.text = roomName
        holder.gsrReservationDateTv.text = day + "\n" + fromHour + "-" + toHour

        holder.gsrReservationCancelButton.setOnClickListener {
            // Use the current adapter position at click time, not the position
            // captured when onBindViewHolder ran, in case the list has shifted.
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener
            val currentReservation = reservations[currentPosition]

            //  val builder = AlertDialog.Builder(mContext)
            AlertDialog
                .Builder(mContext)
                .setTitle("Are you sure?")
                .setMessage("Please confirm that you wish to delete this booking.")
                .setPositiveButton("Confirm") { _, _ ->
                    onCancelRequested(currentReservation, currentPosition)
                }.setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = reservations.size

    fun removeAt(position: Int) {
        if (position < 0 || position >= reservations.size) return
        reservations.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class GsrReservationViewHolder(
        itemBinding: GsrReservationBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val gsrReservationCancelButton = itemBinding.gsrReservationCancelBtn
        val gsrReservationLocationTv = itemBinding.gsrReservationLocationTv
        val gsrReservationDateTv = itemBinding.gsrReservationDateTv
        val gsrReservationIv = itemBinding.gsrReservationIv
    }
}
