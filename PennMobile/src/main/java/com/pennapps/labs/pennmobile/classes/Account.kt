package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Account (firstName: String?, lastName: String?, pennkey: String?, email: String?,
               affiliation: Array<String>?) {

    val first_name = firstName
    val last_name = lastName
    val pennkey = pennkey
    val email = email
    val affiliation = affiliation

//    @SerializedName("first_name")
//    @Expose
//    val firstName: String? = null
//    @SerializedName("last_name")
//    @Expose
//    val lastName: String? = null
//    @SerializedName("pennid")
//    @Expose
//    val pennid: Int? = null
//    @SerializedName("username")
//    @Expose
//    val username: String? = null
//    @SerializedName("email")
//    @Expose
//    val email: String? = null
//    @SerializedName("affiliation")
//    @Expose
//    val affiliation: Array<String>? = null

}