package com.pennapps.labs.pennmobile.gsr.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pennapps.labs.pennmobile.databinding.GsrReservationBinding
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class GsrReservationsAdapter(
    private var reservations: ArrayList<GSRReservation>,
    private val onCancelRequested: (GSRReservation) -> Unit,
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
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val currentReservation = reservations[currentPosition]

            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Are you sure?")
                .setMessage("Please confirm that you wish to delete this booking.")
                .setPositiveButton("Confirm") { _, _ ->
                    onCancelRequested(currentReservation)
                }.setNegativeButton("Cancel", null)
                .show()
        }

        holder.gsrReservationShareButton.setOnClickListener {
            val bearerToken =
                "Bearer " +
                    PreferenceManager
                        .getDefaultSharedPreferences(mContext)
                        .getString(mContext.getString(R.string.access_token), "")

            MainActivity.studentLifeInstance
                .getGsrShareCode(bearerToken, ShareCodeRequest(reservation.bookingId ?: return@setOnClickListener))
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(
                    rx.android.schedulers.AndroidSchedulers
                        .mainThread(),
                ).subscribe({ response ->
                    val deepLink = "https://pennmobile.org/gsr/share?data=${response.code}"

                    val shareIntent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "GSR Booking: $roomName")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join my GSR booking!\n$roomName\n$day, $fromHour - $toHour\n\n$deepLink",
                            )
                        }
                    mContext.startActivity(Intent.createChooser(shareIntent, "Share Reservation"))
                }, { error ->
                    error.printStackTrace()
                    Toast.makeText(mContext, "Failed to generate share link.", Toast.LENGTH_SHORT).show()
                })
        }
    }

    override fun getItemCount(): Int = reservations.size

    fun indexOfBookingId(bookingId: String?): Int? {
        if (bookingId == null) return null
        return reservations
            .indexOfFirst { it.bookingId == bookingId }
            .takeIf { it >= 0 }
    }

    fun removeAt(position: Int) {
        if (position < 0 || position >= reservations.size) return
        reservations.removeAt(position)
        notifyItemRemoved(position)
    }

    class GsrReservationViewHolder(
        itemBinding: GsrReservationBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val gsrReservationCancelButton = itemBinding.gsrReservationCancelBtn
        val gsrReservationShareButton = itemBinding.gsrReservationShareBtn
        val gsrReservationLocationTv = itemBinding.gsrReservationLocationTv
        val gsrReservationDateTv = itemBinding.gsrReservationDateTv
        val gsrReservationIv = itemBinding.gsrReservationIv
    }
}
