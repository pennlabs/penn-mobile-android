package com.pennapps.labs.pennmobile.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.databinding.GsrListItemBinding
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.fragments.GsrTabbedFragment
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class HomeGsrReservationAdapter(
    reservations: List<GSRReservation>,
) : RecyclerView.Adapter<HomeGsrReservationAdapter.GSRReservationViewHolder>() {
    private var activeReservations: List<GSRReservation> = reservations

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GSRReservationViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity

        val itemBinding = GsrListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GSRReservationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: GSRReservationViewHolder,
        position: Int,
    ) {
        val currentReservation = activeReservations[position]
        val location = currentReservation.name
        val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val from = formatter.parseDateTime(currentReservation.fromDate)
        val to = formatter.parseDateTime(currentReservation.toDate)
        val day = from.toString("EEEE, MMMM d")
        val fromHour = from.toString("h:mm a")
        val toHour = to.toString("h:mm a")

        val imageUrl = currentReservation.info?.get("thumbnail") ?: "https://s3.us-east-2.amazonaws.com/labs.api/dining/MBA+Cafe.jpg"
        Picasso
            .get()
            .load(imageUrl)
            .fit()
            .centerCrop()
            .into(holder.itemImage)

        holder.itemLocation.text = location
        holder.itemDate.text = day + "\n" + fromHour + "-" + toHour

        holder.itemView.setOnClickListener {
            // Moves to GSR Booking Tab
            mActivity.setTab(MainActivity.GSR_ID)

            // Changes tab to "My Reservations" tab (from "Book a Room" tab)
            for (fragment in mActivity.supportFragmentManager.fragments) {
                if (fragment is GsrTabbedFragment) {
                    fragment.viewPager.currentItem = 1
                }
            }
        }
    }

    override fun getItemCount(): Int = activeReservations.size

    inner class GSRReservationViewHolder(
        itemBinding: GsrListItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        val itemImage: ImageView = itemBinding.itemGsrImage
        val itemLocation: TextView = itemBinding.itemGsrLocation
        val itemDate: TextView = itemBinding.itemGsrDate
    }
}
