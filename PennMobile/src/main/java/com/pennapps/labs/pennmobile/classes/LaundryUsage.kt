package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/25/2018.
 */

class LaundryUsage {
    @SerializedName("day_of_week")
    @Expose
    private val dayOfWeek: String? = null
    @SerializedName("dryer_data")
    @Expose
    var dryerData: MachineData? = null
        private set
    @SerializedName("end_date")
    @Expose
    private val endDate: String? = null
    @SerializedName("hall_name")
    @Expose
    val hallName: String? = null
    @SerializedName("location")
    @Expose
    private val location: String? = null
    @SerializedName("start_date")
    @Expose
    private val startDate: String? = null
    @SerializedName("total_number_of_dryers")
    @Expose
    private val totalNumberOfDryers: Double? = null
    @SerializedName("total_number_of_washers")
    @Expose
    private val totalNumberOfWashers: Double? = null
    @SerializedName("washer_data")
    @Expose
    var washerData: MachineData? = null
        private set
    var id: Int = 0

    fun setDryerData() {
        dryerData = MachineData()
        dryerData!!.setData()
    }

    fun setWasherData() {
        washerData = MachineData()
        washerData!!.setData()
    }
}
