package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PollPop {

    @SerializedName("id")
    @Expose
    private val id : Int? = 0

    @SerializedName("kind")
    @Expose
    private val kind : String? = ""

    @SerializedName("population")
    @Expose
    private val population : String? = ""


    override fun toString(): String {
        return "PollPop(id=$id, kind=$kind, population=$population)"
    }


}