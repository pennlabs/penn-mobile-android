package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiningPreferences {
    @SerializedName("preferences")
    @Expose
    val preferences: List<DiningHallPreference>? = null
}