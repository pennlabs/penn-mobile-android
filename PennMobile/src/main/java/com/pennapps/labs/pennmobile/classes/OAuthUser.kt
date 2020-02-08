package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OAuthUser {
    @SerializedName("first_name")
    @Expose
    val firstName: String? = null
    @SerializedName("last_name")
    @Expose
    val lastName: String? = null
    @SerializedName("pennid")
    @Expose
    val pennid: Int? = null
    @SerializedName("username")
    @Expose
    val username: String? = null
    @SerializedName("email")
    @Expose
    val email: String? = null
    @SerializedName("affiliation")
    @Expose
    val affiliation: Array<String>? = null
}