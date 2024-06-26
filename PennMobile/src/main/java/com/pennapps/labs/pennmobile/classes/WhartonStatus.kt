package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WhartonStatus {
    @SerializedName("is_wharton")
    @Expose
    @JvmField
    var isWharton: Boolean = false
}
