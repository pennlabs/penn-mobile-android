package com.pennapps.labs.pennmobile.Subletting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R

class OfferListAdapter(private val dataModel : OfferViewModel):
    RecyclerView.Adapter<OfferListAdapter.OfferViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var offerName = itemView.findViewById<TextView>(R.id.name_text)
        var phoneNumber = itemView.findViewById<TextView>(R.id.phone_number_text)
        var dates = itemView.findViewById<TextView>(R.id.dates_text)
        var message = itemView.findViewById<TextView>(R.id.message_text)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.sublet_candidate_item, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity
        return OfferViewHolder(newView)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {

        var offer: Offer = dataModel.getOffer(position) // dataModel.getSblet(position)
        holder.offerName.text = "Name: " + offer.user
        holder.phoneNumber.text = "Phone Number: " + offer.phoneNumber
        holder.dates.text = "Created: " + offer.createdDate
        holder.message.text = "Message: " + offer.message



    }

    override fun getItemCount(): Int {
        return dataModel.offersList.value?.size ?: 0
    }









}