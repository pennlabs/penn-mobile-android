package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//class that keeps track of all the GSR rooms themselves
class GSR2 {

    @SerializedName("id")
    @Expose
    var id : Int? = null

    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("lid")
    @Expose
    var lid: String? = null

    @SerializedName("gid")
    @Expose
    var gid: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("image_url")
    @Expose
    var image_url: String? = null
}
