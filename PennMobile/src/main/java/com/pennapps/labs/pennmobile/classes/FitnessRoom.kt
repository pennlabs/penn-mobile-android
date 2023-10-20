package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FitnessRoom {
    @SerializedName("id")
    @Expose
    var roomId: Int? = null

    @SerializedName("name")
    @Expose
    var roomName: String? = null

    @SerializedName("last_updated")
    @Expose
    var lastUpdated: String? = null

    @SerializedName("count")
    @Expose
    var count: Int? = null

    @SerializedName("capacity")
    @Expose
    var capacity: Float? = null

    @SerializedName("open")
    @Expose
    var openTimeList: List<String>? = null

    @SerializedName("close")
    @Expose
    var closeTimeList: List<String>? = null

    @SerializedName("image_url")
    @Expose
    var imageURL: String? = null
}