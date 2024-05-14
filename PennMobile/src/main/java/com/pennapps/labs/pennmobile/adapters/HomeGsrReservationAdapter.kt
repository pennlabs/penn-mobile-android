package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.GsrTabbedFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_date
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_image
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_location
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class HomeGsrReservationAdapter(reservations: List<GSRReservation>) : RecyclerView.Adapter<HomeGsrReservationAdapter.ViewHolder>() {
    private var activeReservations: List<GSRReservation> = reservations

    private lateinit var itemImage: ImageView
    private lateinit var itemLocation: TextView
    private lateinit var itemDate: TextView

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife

    override fun onBindViewHolder(
        holder: HomeGsrReservationAdapter.ViewHolder,
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
        Picasso.get().load(imageUrl).fit().centerCrop().into(holder.itemView.item_gsr_image)

        holder.itemView.item_gsr_location.text = location
        holder.itemView.item_gsr_date.text = day + "\n" + fromHour + "-" + toHour

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

    override fun getItemCount(): Int {
        return activeReservations.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeGsrReservationAdapter.ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance

        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_list_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemImage = itemView.item_gsr_image
            itemLocation = itemView.item_gsr_location
            itemDate = itemView.item_gsr_date
        }
    }
}
