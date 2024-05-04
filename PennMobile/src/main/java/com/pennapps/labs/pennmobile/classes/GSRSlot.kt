package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// class that represents an available session for a given GSR room

/**
 * Created by MikeD on 10/14/2018.
 */

class GSRSlot {
    @SerializedName("available")
    @Expose
    var isAvailable: Boolean = true

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null
}
