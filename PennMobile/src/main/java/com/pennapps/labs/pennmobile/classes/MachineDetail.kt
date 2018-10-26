package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/26/2018.
 */

class MachineDetail : Comparable<MachineDetail> {

    @SerializedName("id")
    @Expose
    val id: Int = 0

    @SerializedName("status")
    @Expose
    val status: String? = null

    @SerializedName("time_remaining")
    @Expose
    var timeRemaining: Int = 0

    @SerializedName("type")
    @Expose
    val type: String? = null

    override fun compareTo(machineDetail: MachineDetail): Int {

        return if (timeRemaining == machineDetail.timeRemaining) {
            0
        } else if (timeRemaining > machineDetail.timeRemaining) {
            1
        } else {
            -1
        }
    }
}