package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class PCARegistrationBody(
    @SerializedName("section") val section: String?,
    @SerializedName("auto_resubscribe") val autoResubscribe: Boolean? = false,
    @SerializedName("close_notification") val closeNotification: Boolean? = false
) {
}