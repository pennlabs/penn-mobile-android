package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservation {
    @SerializedName("booking_id")
    @Expose
    @JvmField
    var booking_id: String? = null

    @SerializedName("gsr")
    @Expose
    @JvmField
    var gsr: GSR2? = null

    @SerializedName("room_id")
    @Expose
    @JvmField
    var room_id: Int? = null

    @SerializedName("room_name")
    @Expose
    @JvmField
    var room_name: String? = null

    @SerializedName("start")
    @Expose
    @JvmField
    var start: String? = null

    @SerializedName("end")
    @Expose
    @JvmField
    var end: String? = null
}