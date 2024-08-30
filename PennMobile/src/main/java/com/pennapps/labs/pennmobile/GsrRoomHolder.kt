package com.pennapps.labs.pennmobile

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.gsr_room.view.gsr_end_time
import kotlinx.android.synthetic.main.gsr_room.view.gsr_id
import kotlinx.android.synthetic.main.gsr_room.view.gsr_room
import kotlinx.android.synthetic.main.gsr_room.view.gsr_start_time
import kotlinx.android.synthetic.main.gsr_room.view.locationId

class GsrRoomHolder(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {
    internal var gsrRoom: LinearLayout = itemView.gsr_room
    internal var gsrStartTime: TextView = itemView.gsr_start_time
    internal var gsrEndTime: TextView = itemView.gsr_end_time
    internal var gsrId: TextView = itemView.gsr_id
    internal var locationId: TextView = itemView.locationId
}
