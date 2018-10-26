package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/25/2018.
 */

class LaundryRoomSimple {

    @SerializedName("hall_name")
    @Expose
    @JvmField var name: String? = null
    @SerializedName("id")
    @Expose
    @JvmField var id: Int? = null
    @SerializedName("location")
    @Expose
    @JvmField var location: String? = null

}
