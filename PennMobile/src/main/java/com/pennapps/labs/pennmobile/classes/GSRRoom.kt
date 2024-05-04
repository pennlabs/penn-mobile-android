package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/14/2018.
 */

class GSRRoom {
    // getters

    @SerializedName("capacity")
    @Expose
    var capacity: Int? = null

    @SerializedName("gid")
    @Expose
    var gid: Int? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("room_name")
    @Expose
    var name: String? = null

    @SerializedName("id")
    @Expose
    var roomId: Int? = null

    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null

    @SerializedName("availability")
    @Expose
    var slots: Array<GSRSlot>? = null
}
