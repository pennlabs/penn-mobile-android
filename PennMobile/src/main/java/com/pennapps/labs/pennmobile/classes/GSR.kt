package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/14/2018.
 */

// class that keeps track of all the GSR rooms themselves
class GSR {
    @SerializedName("location_id")
    @Expose
    var location_id: Int? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("rooms")
    @Expose
    var rooms: Array<GSRRoom>? = null
}
