package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("profile")
    val profile: Profile,
    @SerializedName("last_name")
    val lastName: String = "",
    @SerializedName("first_name")
    val firstName: String = "",
    @SerializedName("username")
    val username: String = "",
)
