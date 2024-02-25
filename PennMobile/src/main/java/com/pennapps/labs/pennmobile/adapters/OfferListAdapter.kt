package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.SublettingViewModel

class OfferListAdapter(private val dataModel : SublettingViewModel):
    RecyclerView.Adapter<OfferListAdapter.OfferViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var mActivity: MainActivity

    class OfferViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        //TODO("Not yet implemented")
        val newView = LayoutInflater.from(parent.context).inflate(R.layout.sublet_candidate_item, parent, false)
        mContext = parent.context
        mActivity = mContext as MainActivity
        return OfferViewHolder(newView)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        //TODO("do all the stuff with setting up/adding the data but need the viewmodel first")
        //see example on postedsubletslistadatper
    }

    override fun getItemCount(): Int {
        return 0
        //        return dataModel.postedSubletsList.value?.size ?: 0
    }









}