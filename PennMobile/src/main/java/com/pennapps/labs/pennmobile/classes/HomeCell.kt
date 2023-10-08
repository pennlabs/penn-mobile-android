package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jackie on 2018-03-28.
 */

open class HomeCell {
    @SerializedName("type")
    @Expose
    var type: String? = null

    var reservations: List<GSRReservation>? = null
    var events: List<CalendarEvent>? = null
    var buildings: List<String>? = null

    @SerializedName("info")
    @Expose
    var info: HomeCellInfo? = null
}
