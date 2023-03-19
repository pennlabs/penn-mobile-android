package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PollOption {

    @SerializedName("id")
    @Expose
    private val id : Int? = null

    @SerializedName("poll")
    @Expose
    private val poll : Int? = null

    @SerializedName("choice")
    @Expose
    val choice : String? = null

    @SerializedName("vote_count")
    @Expose
    var voteCount : Int = 0

    var isVisible : Boolean = false
}