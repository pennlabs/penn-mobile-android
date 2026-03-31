package com.pennapps.labs.pennmobile.gsr.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// for GSR sharing
data class ShareCodeRequest(@SerializedName("booking_id") val bookingId: String)
data class ShareCodeResponse(val code: String)

data class GSRShareResponse(
    @SerializedName("booking_id") val bookingId: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String,
    @SerializedName("is_valid") val isValid: Boolean,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("gsr") val gsr: GSRInfo
)

data class GSRInfo(
    @SerializedName("name") val name: String,
    @SerializedName("image_url") val imageUrl: String
)

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

    override fun equals(other: Any?): Boolean =
        other is GSRReservation &&
            this.bookingId == other.bookingId &&
            this.name == other.name &&
            this.fromDate == other.fromDate &&
            this.toDate == other.toDate &&
            this.gid == other.gid &&
            this.lid == other.lid

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
