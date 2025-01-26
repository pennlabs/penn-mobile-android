package com.pennapps.labs.pennmobile.fitness.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitnessPreferences {
    @SerializedName("rooms")
    @Expose
    var rooms: List<Int?>? = null
}
