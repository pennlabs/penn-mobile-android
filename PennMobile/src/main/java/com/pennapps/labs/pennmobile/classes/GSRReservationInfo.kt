package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservationInfo {
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null
}