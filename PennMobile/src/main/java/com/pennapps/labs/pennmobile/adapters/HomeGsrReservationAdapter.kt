package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.pennapps.labs.pennmobile.GsrReservationsFragment
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.MenuFragment
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dining_list_item.view.*
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_date
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_image
import kotlinx.android.synthetic.main.gsr_list_item.view.item_gsr_location
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_cancel_btn
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_date_tv
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_iv
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_location_tv
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import rx.android.schedulers.AndroidSchedulers

class HomeGsrReservationAdapter (reservations: List<GSRReservation>) : RecyclerView.Adapter<HomeGsrReservationAdapter.ViewHolder>() {
    private var activeReservations: List<GSRReservation> = reservations

    private lateinit var itemImage: ImageView
    private lateinit var itemLocation: TextView
    private lateinit var itemDate: TextView


    //TODO("Image and text views")


    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    override fun onBindViewHolder(holder: HomeGsrReservationAdapter.ViewHolder, position: Int) {
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
            mActivity.setTab(MainActivity.GSR_ID)
        }

    }

    override fun getItemCount(): Int {
        return activeReservations.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeGsrReservationAdapter.ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance

        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_list_item, parent, false)
        //view?.dining_progress?.visibility = GONE
        return ViewHolder(view)
    }
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemImage = itemView.item_gsr_image
            itemLocation = itemView.item_gsr_location
            itemDate = itemView.item_gsr_date

        }

    }
}