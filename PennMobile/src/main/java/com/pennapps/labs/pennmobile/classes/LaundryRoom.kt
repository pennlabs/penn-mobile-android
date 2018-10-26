package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LaundryRoom {

    var id: Int = 0
    @SerializedName("hall_name")
    @Expose
    val name: String? = null
    @SerializedName("machines")
    @Expose
    val machines: Machines? = null
}
