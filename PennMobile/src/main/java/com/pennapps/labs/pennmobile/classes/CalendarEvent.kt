package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CalendarEvent {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("end")
    @Expose
    var end: String? = null
    @SerializedName("start")
    @Expose
    var start: String? = null
}