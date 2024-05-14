package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRReservation {
    @SerializedName("booking_id")
    @Expose
    @JvmField
    var bookingId: String? = null

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

    override fun equals(other: Any?): Boolean {
        return other is GSRReservation && this.bookingId == other.bookingId && this.name == other.name &&
            this.fromDate == other.fromDate && this.toDate == other.toDate && this.gid == other.gid &&
            this.lid == other.lid
    }

    override fun hashCode(): Int {
        var result = (bookingId?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (fromDate?.hashCode() ?: 0)
        result = 31 * result + (toDate?.hashCode() ?: 0)
        result = 31 * result + (gid?.hashCode() ?: 0)
        result = 31 * result + (lid?.hashCode() ?: 0)
        result = 31 * result + (info?.hashCode() ?: 0)
        return result
    }
}
