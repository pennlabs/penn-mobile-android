package com.pennapps.labs.pennmobile

import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pennapps.labs.pennmobile.databinding.GsrRoomBinding

class GsrRoomHolder(
    itemBinding: GsrRoomBinding,
) : RecyclerView.ViewHolder(itemBinding.root) {
    internal var gsrRoom: LinearLayout = itemBinding.gsrRoom
    internal var gsrStartTime: TextView = itemBinding.gsrStartTime
    internal var gsrEndTime: TextView = itemBinding.gsrEndTime
    internal var gsrId: TextView = itemBinding.gsrId
    internal var locationId: TextView = itemBinding.locationId
}
