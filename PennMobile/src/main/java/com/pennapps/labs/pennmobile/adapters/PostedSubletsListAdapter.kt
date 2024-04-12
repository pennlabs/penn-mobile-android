package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.SublesseeDetailsFragment
import com.pennapps.labs.pennmobile.Subletting.SubletDetailsFragment
import com.pennapps.labs.pennmobile.Subletting.SubletDetailsHolderFragment
import com.pennapps.labs.pennmobile.classes.Sublet
import com.pennapps.labs.pennmobile.classes.SublettingModel
import com.pennapps.labs.pennmobile.classes.SublettingViewModel

class PostedSubletsListAdapter(private val dataModel: SublettingViewModel):
        RecyclerView.Adapter<PostedSubletsListAdapter.SublettingCardViewHolder>() {

    //get datamodel - this has instance of datamodel where it can access data from.
    //fragment will say when to update

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    class SublettingCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var listingImage = itemView.findViewById<ImageView>(R.id.subletting_cardview_image)
        var listingTitle = itemView.findViewById<TextView>(R.id.subletting_cardview_title)
        var listingPrice = itemView.findViewById<TextView>(R.id.subletting_cardview_price)
        var listingRooms = itemView.findViewById<TextView>(R.id.subletting_cardview_rooms)
        var listingDates = itemView.findViewById<TextView>(R.id.subletting_cardview_dates)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SublettingCardViewHolder {
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.subletting_cardview, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity
        return SublettingCardViewHolder(newView)
    }

    override fun onBindViewHolder(holder: SublettingCardViewHolder, position: Int) {

        var mSublettingCard: Sublet = dataModel.getSublet(position) // dataModel.getSblet(position)
        //holder.listingImage.setImageResource(mSublettingCard.listingImage!!)

        /* Glide.with(mContext) // Use mContext here instead of context
            .load(mSublettingCard.images?.get(0)?.imageUrl) // Access the first image URL from the list
            .centerCrop() // optional - adjust as needed
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.listingImage) */

        holder.listingTitle.text = mSublettingCard.title

        var price = "$" + mSublettingCard.price.toString()

        holder.listingPrice.text = price

        val rooms = mSublettingCard.beds.toString() + " bd | " +
                mSublettingCard.baths.toString() + " ba"
        holder.listingRooms.text = rooms

        holder.itemView.setOnClickListener {
            mActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, SubletDetailsHolderFragment(dataModel, position))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
        }
    }


    override fun getItemCount(): Int {
        return dataModel.postedSubletsList.value?.size ?: 0
    }

}