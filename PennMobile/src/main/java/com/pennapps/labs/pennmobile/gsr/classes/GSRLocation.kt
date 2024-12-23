package com.pennapps.labs.pennmobile.gsr.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/14/2018.
 */

class GSRLocation {
    @SerializedName("name")
    @Expose
    @JvmField
    var name: String? = null

    @SerializedName("lid")
    @Expose
    @JvmField
    var id: String? = null

    @SerializedName("gid")
    @Expose
    @JvmField
    var gid: Int = 0

    @SerializedName("kind")
    @Expose
    @JvmField
    var kind: String? = null
}
