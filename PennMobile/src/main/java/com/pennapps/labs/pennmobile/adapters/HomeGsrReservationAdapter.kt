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
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.MenuFragment
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.classes.GSRReservation
import kotlinx.android.synthetic.main.dining_list_item.view.*
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_cancel_btn
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_date_tv
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_iv
import kotlinx.android.synthetic.main.gsr_reservation.view.gsr_reservation_location_tv
import rx.android.schedulers.AndroidSchedulers

class HomeGsrReservationAdapter (reservations: List<GSRReservation>) : RecyclerView.Adapter<HomeGsrReservationAdapter.ViewHolder>() {
    private var activeReservations: List<GSRReservation> = reservations

    private lateinit var itemImage: ImageView
    private lateinit var itemLocation: TextView
    private lateinit var itemButton: Button
    private lateinit var itemDate: TextView

    //TODO("Image and text views")


    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife
    override fun onBindViewHolder(holder: HomeGsrReservationAdapter.ViewHolder, position: Int) {
        val currentReservation = activeReservations[position]
        //get image
        //get name
        itemLocation.text = currentReservation.name

        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return activeReservations.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeGsrReservationAdapter.ViewHolder {
        mContext = parent.context
        mActivity = mContext as MainActivity
        mStudentLife = MainActivity.studentLifeInstance

        val view = LayoutInflater.from(parent.context).inflate(R.layout.gsr_reservation, parent, false)
        //view?.dining_progress?.visibility = GONE
        return ViewHolder(view)
    }
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            TODO("Put shit here and look at dining card adapter")
            itemImage = itemView.gsr_reservation_iv
            itemLocation = itemView.gsr_reservation_location_tv
            itemButton = itemView.gsr_reservation_cancel_btn
            itemDate = itemView.gsr_reservation_date_tv
        }

    }
}