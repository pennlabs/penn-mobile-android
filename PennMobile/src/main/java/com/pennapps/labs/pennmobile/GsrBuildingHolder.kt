package com.pennapps.labs.pennmobile

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.gsr_building.view.*


class GsrBuildingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var gsrBuildingName: TextView? = null
    internal var recyclerView: RecyclerView? = null

    init {
        ButterKnife.bind(this, itemView)
        recyclerView = itemView.gsr_availability_in_building
        gsrBuildingName = itemView.gsr_building_name

    }
}
