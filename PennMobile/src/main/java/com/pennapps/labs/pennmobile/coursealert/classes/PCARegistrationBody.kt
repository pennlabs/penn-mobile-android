package com.pennapps.labs.pennmobile.coursealert.classes

import com.google.gson.annotations.SerializedName

/**
 * Created by Ali Krema, Fall 2022.
 */

data class PCARegistrationBody(
    @SerializedName("section") val section: String?,
    @SerializedName("auto_resubscribe") val autoResubscribe: Boolean? = false,
    @SerializedName("close_notification") val closeNotification: Boolean? = false,
)
