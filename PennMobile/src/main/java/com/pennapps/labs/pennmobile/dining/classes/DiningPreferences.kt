package com.pennapps.labs.pennmobile.dining.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiningPreferences {
    @SerializedName("preferences")
    @Expose
    val preferences: List<DiningHallPreference>? = null
}
