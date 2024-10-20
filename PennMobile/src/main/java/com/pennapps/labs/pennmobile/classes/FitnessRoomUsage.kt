package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitnessRoomUsage {
    @SerializedName("room_name")
    @Expose
    var roomName: String? = null

    @SerializedName("start_date")
    @Expose
    var startDate: String? = null

    @SerializedName("end_date")
    @Expose
    var endDate: String? = null

    @SerializedName("usage")
    @Expose
    var roomUsage: Map<String?, Float?>? = null
}
