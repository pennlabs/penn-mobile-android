package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.SublesseeDetailsFragment
import com.pennapps.labs.pennmobile.SubletteeMarketplace
import com.pennapps.labs.pennmobile.classes.SublesseeViewModel
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingModel

class SublettingListAdapter(var sublettingList: ArrayList<SublettingModel>, var dataModel: SublesseeViewModel):
        RecyclerView.Adapter<SublettingListAdapter.SublettingCardViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity
    //private lateinit var dataModel: SublesseeViewModel
    //private lateinit var actualSublets: ArrayList<Sublet>

    class SublettingCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var listingImage = itemView.findViewById<ImageView>(R.id.subletting_cardview_image)
        var listingTitle = itemView.findViewById<TextView>(R.id.subletting_cardview_title)
        var listingPrice = itemView.findViewById<TextView>(R.id.subletting_cardview_price)
        var listingRooms = itemView.findViewById<TextView>(R.id.subletting_cardview_rooms)
        var listingDates = itemView.findViewById<TextView>(R.id.subletting_cardview_dates)

        /* init {
            itemView.setOnClickListener {
                mActivity.supportFragmentManager.beginTransaction()
                        .replace(itemView.id, SubletteeFragment())
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
            }
        } */

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SublettingCardViewHolder {
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.subletting_cardview, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity
        return SublettingCardViewHolder(newView)
    }

    override fun onBindViewHolder(holder: SublettingCardViewHolder, position: Int) {

        val actualSublet = dataModel.getSublet(position)

        holder.listingTitle.text = actualSublet.title
        var price = "$" + actualSublet.price.toString()
        holder.listingPrice.text = price
        val rooms = actualSublet.beds.toString() + " bd | " +
                actualSublet.baths.toString() + " ba"
        holder.listingRooms.text = rooms

        holder.listingDates.text = buildString {
            append(actualSublet.startDate)
            append(" - ")
            append(actualSublet.endDate)
        }

        holder.itemView.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, SublesseeDetailsFragment(dataModel, position))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun getItemCount(): Int {
        if (dataModel.getSublettingList() == null) {
            return 0
        } else {
            return dataModel.getSublettingList()!!.size
        }
    }
}