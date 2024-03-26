package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitnessPreferences {
    @SerializedName("rooms")
    @Expose
    val rooms: List<Int>? = null
}