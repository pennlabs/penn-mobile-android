package com.pennapps.labs.pennmobile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PollResult {
    @SerializedName("results")
    @Expose
    private val results: Boolean? = null

    @SerializedName("error")
    @Expose
    private val error: String? = null

    fun getResults(): Boolean? {
        return results
    }

    fun getError(): String? {
        return error
    }
}