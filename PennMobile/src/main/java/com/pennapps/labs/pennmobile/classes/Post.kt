package com.pennapps.labs.pennmobile.classes

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

class Post {

    @SerializedName("id")
    @Expose
    internal val id : Int? = 0

    @SerializedName("club_code")
    @Expose
    internal val club_code : String? = ""

    @SerializedName("title")
    @Expose
    internal val title : String? = ""

    @SerializedName("subtitle")
    @Expose
    internal val subtitle : String? = ""

    @SerializedName("post_url")
    @Expose
    internal val post_url : String? = ""

    @SerializedName("image_url")
    @Expose
    internal val image_url : String? = ""

    @SerializedName("created_date")
    @Expose
    internal val created_date : String? = ""

    @SerializedName("start_date")
    @Expose
    internal val start_date : String? = ""

    @SerializedName("expire_date")
    @Expose
    internal val expire_date : String? = ""


    @SerializedName("club_comment")
    @Expose
    internal val club_comment : String? = ""

    @SerializedName("admin_comment")
    @Expose
    internal val admin_comment : String? = ""

    @SerializedName("status")
    @Expose
    internal val status : String? = ""

    @SerializedName("target_populations")
    @Expose
    internal val target_populations : List<Int>? = null

    override fun toString(): String {
        return id.toString() + ", " +
                club_code + ""
    }

    fun getResults(): Any {
        return true
    }
}