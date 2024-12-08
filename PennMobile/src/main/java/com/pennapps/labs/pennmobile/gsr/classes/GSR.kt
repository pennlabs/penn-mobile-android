package com.pennapps.labs.pennmobile.gsr.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// class that keeps track of all the GSR rooms themselves

/**
 * Created by Varun on 10/14/2018.
 */

class GSR {
    @SerializedName("location_id")
    @Expose
    var locationId: Int? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("rooms")
    @Expose
    var rooms: Array<GSRRoom>? = null
}
