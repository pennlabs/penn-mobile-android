package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class PCARegistrationBody (
    @SerializedName("id") val id: Int?,
    @SerializedName("section") val section: String?,
    @SerializedName("auto_subscribe") val autoSubscribe: Boolean? = false,
    @SerializedName("close_notification") val closeNotification: Boolean? = false
    ) {
    //TODO: (ALI) enforce constraints + required "section" using init block
}