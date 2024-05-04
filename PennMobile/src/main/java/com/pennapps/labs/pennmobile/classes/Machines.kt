package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Machines {
    @SerializedName("dryers")
    @Expose
    val dryers: MachineList? = null

    @SerializedName("washers")
    @Expose
    val washers: MachineList? = null

    @SerializedName("details")
    @Expose
    val machineDetailList: List<MachineDetail>? = null
}
