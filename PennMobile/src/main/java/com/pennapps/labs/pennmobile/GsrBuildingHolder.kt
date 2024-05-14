package com.pennapps.labs.pennmobile

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gsr_building.view.gsr_availability_in_building
import kotlinx.android.synthetic.main.gsr_building.view.gsr_building_name

class GsrBuildingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var gsrBuildingName: TextView? = null
    internal var recyclerView: RecyclerView? = null

    init {
        recyclerView = itemView.gsr_availability_in_building
        gsrBuildingName = itemView.gsr_building_name
    }
}
