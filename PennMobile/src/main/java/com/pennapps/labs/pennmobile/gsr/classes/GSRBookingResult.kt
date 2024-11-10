package com.pennapps.labs.pennmobile.gsr.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GSRBookingResult {
    @SerializedName("results")
    @Expose
    private val results: Boolean? = null

    @SerializedName("detail")
    @Expose
    private val detail: String? = null

    @SerializedName("error")
    @Expose
    private val error: String? = null

    fun getDetail(): String? = detail

    fun getResults(): Boolean? = results

    fun getError(): String? = error
}
