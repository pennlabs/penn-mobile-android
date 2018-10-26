package com.pennapps.labs.pennmobile

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.gsr_room.view.*

class GsrRoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var gsrRoom: LinearLayout? = null
    internal var gsrStartTime: TextView? = null
    internal var gsrEndTime: TextView? = null
    internal var gsrId: TextView? = null
    internal var locationId: TextView? = null

    init {
        ButterKnife.bind(this, itemView)
        gsrRoom = itemView.gsr_room
        gsrStartTime = itemView.gsr_start_time
        gsrEndTime = itemView.gsr_end_time
        gsrId = itemView.gsr_id
        locationId = itemView.locationId

    }


}
