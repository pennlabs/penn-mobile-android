package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by MikeD on 10/14/2018.
 */

//class that represents an available session for a given GSR room
class GSRSlot {

    @SerializedName("available")
    @Expose
    var isAvailable: Boolean = false

    @SerializedName("start")
    @Expose
    var startTime: String? = null

    @SerializedName("end")
    @Expose
    var endTime: String? = null
}