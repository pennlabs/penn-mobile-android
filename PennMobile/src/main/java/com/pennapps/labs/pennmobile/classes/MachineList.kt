package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/26/2018.
 */

class MachineList {
    @SerializedName("offline")
    @Expose
    val offline: Int? = null

    @SerializedName("open")
    @Expose
    val open: Int? = null

    @SerializedName("out_of_order")
    @Expose
    val outOfOrder: Int? = null

    @SerializedName("running")
    @Expose
    val running: Int? = null

    @SerializedName("time_remaining")
    @Expose
    val timeRemaining: List<Int>? = null
}
