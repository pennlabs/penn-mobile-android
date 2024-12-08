package com.pennapps.labs.pennmobile.home.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CalendarEvent {
    @SerializedName("event")
    @Expose
    var name: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    override fun equals(other: Any?): Boolean =
        when (other) {
            is CalendarEvent -> {
                this.name == other.name && this.date == other.date
            } else -> false
        }

    override fun hashCode(): Int {
        // lazy hash function but we don't use this method anyways
        val nameHash = name.hashCode().toString()
        val dateHash = date.hashCode().toString()
        return (nameHash + dateHash).hashCode()
    }
}
