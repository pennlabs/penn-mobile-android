package com.pennapps.labs.pennmobile.api.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampusExpressAccessTokenResponse {
    @SerializedName("expires_in")
    @Expose
    var expiresIn: Long? = null

    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null
}
