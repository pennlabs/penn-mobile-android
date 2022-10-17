package com.pennapps.labs.pennmobile.classes

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

class Post {

    @SerializedName("id")
    @Expose
    private val id : Int? = 0

    @SerializedName("club_code")
    @Expose
    private val club_code : String? = ""

    @SerializedName("title")
    @Expose
    private val title : String? = ""

    @SerializedName("subtitle")
    @Expose
    private val subtitle : String? = ""

    @SerializedName("post_url")
    @Expose
    private val post_url : String? = ""

    @SerializedName("image_url")
    @Expose
    private val image_url : String? = ""

    @SerializedName("created_date")
    @Expose
    private val created_date : String? = ""

    @SerializedName("start_date")
    @Expose
    private val start_date : String? = ""

    @SerializedName("expire_date")
    @Expose
    private val expire_date : String? = ""


    @SerializedName("club_comment")
    @Expose
    private val club_comment : String? = ""

    @SerializedName("admin_comment")
    @Expose
    private val admin_comment : String? = ""

    @SerializedName("status")
    @Expose
    private val status : String? = ""

    @SerializedName("target_populations")
    @Expose
    private val target_populations : List<Int>? = null

    override fun toString(): String {
        return id.toString() + club_code + title + subtitle
    }

    fun getResults(): Any {
        return true
    }
}