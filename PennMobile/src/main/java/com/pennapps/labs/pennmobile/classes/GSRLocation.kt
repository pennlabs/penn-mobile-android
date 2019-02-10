package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/14/2018.
 */

class GSRLocation {

    @SerializedName("name")
    @Expose
    @JvmField var name: String? = null

    @SerializedName("lid")
    @Expose
    @JvmField var id: Int = 0

    @SerializedName("service")
    @Expose
    var service: String? = null
}