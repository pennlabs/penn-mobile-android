package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservation {
    @SerializedName("booking_id")
    @Expose
    var booking_id: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("fromDate")
    @Expose
    var fromDate: String? = null

    @SerializedName("toDate")
    @Expose
    var toDate: String? = null

    @SerializedName("gid")
    @Expose
    var gid: String? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("info")
    @Expose
    var info: Map<String, String>? = null
}