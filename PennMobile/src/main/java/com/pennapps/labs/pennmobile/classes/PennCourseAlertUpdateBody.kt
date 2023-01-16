package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class PennCourseAlertUpdateBody(@SerializedName("cancelled") val cancelled: Boolean? = false,
                                     @SerializedName("deleted") val deleted: Boolean? = false,
                                     @SerializedName("auto_resubscribe") val autoResubscribe: Boolean? = true,
                                     @SerializedName("close_notification") val closeNotifications: Boolean? = false,
                                     @SerializedName("resubscribe") val resubscribe: Boolean? = false
                                     )