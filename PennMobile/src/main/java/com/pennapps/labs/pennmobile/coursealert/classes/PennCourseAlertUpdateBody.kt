package com.pennapps.labs.pennmobile.coursealert.classes

import com.google.gson.annotations.SerializedName

/**
 * Created by Ali Krema, Fall 2022.
 */

data class PennCourseAlertUpdateBody(
    @SerializedName("cancelled") val cancelled: Boolean? = false,
    @SerializedName("deleted") val deleted: Boolean? = false,
    @SerializedName("auto_resubscribe") val autoResubscribe: Boolean? = true,
    @SerializedName("close_notification") val closeNotifications: Boolean? = false,
    @SerializedName("resubscribe") val resubscribe: Boolean? = false,
)
