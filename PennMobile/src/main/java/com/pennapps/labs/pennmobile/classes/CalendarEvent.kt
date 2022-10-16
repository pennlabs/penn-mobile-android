package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CalendarEvent {
    @SerializedName("event")
    @Expose
    var name: String? = null
    @SerializedName("date")
    @Expose
    var date: String? = null
}