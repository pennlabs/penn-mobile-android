package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservation {
    @SerializedName("booking_id")
    @Expose
    @JvmField
    var booking_id: String? = null

    @SerializedName("name")
    @Expose
    @JvmField
    var name: String? = null

    @SerializedName("fromDate")
    @Expose
    @JvmField
    var fromDate: String? = null

    @SerializedName("toDate")
    @Expose
    @JvmField
    var toDate: String? = null

    @SerializedName("gid")
    @Expose
    @JvmField
    var gid: String? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("info")
    @Expose
    @JvmField
    var info: Map<String, String>? = null
}