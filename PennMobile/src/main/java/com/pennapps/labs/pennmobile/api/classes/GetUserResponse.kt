package com.pennapps.labs.pennmobile.api.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserResponse {
    @SerializedName("user")
    @Expose
    val user: OAuthUser? = null
}
