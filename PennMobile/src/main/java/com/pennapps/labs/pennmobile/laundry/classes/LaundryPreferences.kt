package com.pennapps.labs.pennmobile.laundry.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LaundryPreferences {
    @SerializedName("rooms")
    @Expose
    val rooms: List<Int>? = null
}
