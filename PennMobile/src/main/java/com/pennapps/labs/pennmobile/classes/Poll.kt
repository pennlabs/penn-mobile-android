package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class Poll{
    @SerializedName("id")
    @Expose
    private val id : Int? = 0

    @SerializedName("source")
    @Expose
    private val source : String? = ""

    @SerializedName("question")
    @Expose
    private val question : String? = ""

    @SerializedName("created_date")
    @Expose
    private val created_date : String? = ""

    @SerializedName("start_date")
    @Expose
    private val start_date : String? = ""

    @SerializedName("expire_date")
    @Expose
    private val expire_date : String? = ""

    @SerializedName("multiselect")
    @Expose
    private val multiselect : Boolean? = false

    @SerializedName("user_comment")
    @Expose
    private val user_comment : String? = ""

    @SerializedName("options")
    @Expose
    private val options : JSONObject? = null

    @SerializedName("target_populations")
    @Expose
    private val target_populations : JSONObject? = null
}