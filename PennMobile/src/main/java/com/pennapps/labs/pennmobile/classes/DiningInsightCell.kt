package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class DiningInsightCell {
    @SerializedName("type")
    @Expose
    var type: String? = null
}