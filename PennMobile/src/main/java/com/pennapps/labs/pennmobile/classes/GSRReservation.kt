package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservation {
    @SerializedName("room_name")
    @Expose
    var room_name: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("time")
    @Expose
    var time: String? = null

    @SerializedName("image_url")
    @Expose
    var image_url: String? = null
}