package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.SublettingModel

class SublettingListAdapter(var ctx: android.content.Context?, var sublettingList: ArrayList<SublettingModel>):
        RecyclerView.Adapter<SublettingListAdapter.SublettingCardViewHolder>() {

    private lateinit var context: Context

    class SublettingCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var listingImage = itemView.findViewById<ImageView>(R.id.subletting_cardview_image)
        var listingTitle = itemView.findViewById<TextView>(R.id.subletting_cardview_title)
        var listingPrice = itemView.findViewById<TextView>(R.id.subletting_cardview_price)
        var listingRooms = itemView.findViewById<TextView>(R.id.subletting_cardview_rooms)
        var listingDates = itemView.findViewById<TextView>(R.id.subletting_cardview_dates)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SublettingCardViewHolder {
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.subletting_cardview, parent, false)
        return SublettingCardViewHolder(newView)
    }

    override fun onBindViewHolder(holder: SublettingCardViewHolder, position: Int) {
        var mSublettingCard: SublettingModel = sublettingList[position]
        holder.listingImage.setImageResource(mSublettingCard.listingImage!!)
        holder.listingTitle.text = mSublettingCard.listingTitle

        //price, adding negotiable if price is negotiable
        var price = "$" + mSublettingCard.listingPrice.toString()
        if (mSublettingCard.isNegotiable == true) {
            price += " (negotiable)"
        }
        holder.listingPrice.text = price

        val rooms = mSublettingCard.numberBeds.toString() + " bd | " +
                mSublettingCard.numberBath.toString() + " ba"
        holder.listingRooms.text = rooms
    }

    override fun getItemCount(): Int {
        return sublettingList.size
    }
}