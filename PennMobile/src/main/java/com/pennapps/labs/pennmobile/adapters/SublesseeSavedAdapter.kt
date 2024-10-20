package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.SublesseeDetailsFragment
import com.pennapps.labs.pennmobile.SubletteeMarketplace
import com.pennapps.labs.pennmobile.classes.SublesseeViewModel
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingModel

//for the listview for the saved sublets
class SublesseeSavedAdapter(var dataModel: SublesseeViewModel):
        RecyclerView.Adapter<SublesseeSavedAdapter.SublesseeSavedItemViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity


    class SublesseeSavedItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var listingImage = itemView.findViewById<ImageView>(R.id.subletting_saved_image)
        var listingTitle = itemView.findViewById<TextView>(R.id.subletting_saved_title)
        var listingPrice = itemView.findViewById<TextView>(R.id.subletting_saved_price)
        var listingRooms = itemView.findViewById<TextView>(R.id.subletting_saved_rooms)
        var listingDates = itemView.findViewById<TextView>(R.id.subletting_saved_dates)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SublesseeSavedItemViewHolder {
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.subletting_saved_listview, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity

        return SublesseeSavedItemViewHolder(newView)
    }

    override fun onBindViewHolder(holder: SublesseeSavedItemViewHolder, position: Int) {

        val actualSublet = dataModel.getSavedSublet(position)

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
                    .replace(R.id.content_frame, SublesseeDetailsFragment(dataModel, position, true))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun getItemCount(): Int {
        if (dataModel.getSavedSubletsList() == null) {
            return 0
        } else {
            return dataModel.getSavedSubletsList()!!.size
        }
    }
}