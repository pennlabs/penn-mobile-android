package com.pennapps.labs.pennmobile.gsr.viewholders

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.GsrBuildingBinding

class GsrBuildingHolder(
    itemBinding: GsrBuildingBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    internal var gsrBuildingName: TextView = itemBinding.gsrBuildingName
    internal var recyclerView: RecyclerView? = itemBinding.gsrAvailabilityInBuilding
}
