package com.pennapps.labs.pennmobile.laundry.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/25/2018.
 */

class LaundryRoomSimple {
    @SerializedName("name")
    @Expose
    @JvmField
    var name: String? = null

    @SerializedName("hall_id")
    @Expose
    @JvmField
    var id: Int? = null

    @SerializedName("location")
    @Expose
    @JvmField
    var location: String? = null
}
