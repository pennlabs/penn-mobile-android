package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Rohan Chhaya, Fall 2022.
 * Data model for custom posts on homepage
 */
class Post {

    @SerializedName("id")
    @Expose
    internal val id : Int? = 0

    @SerializedName("club_code")
    @Expose
    internal val clubCode : String? = ""

    @SerializedName("title")
    @Expose
    internal val title : String? = ""

    @SerializedName("subtitle")
    @Expose
    internal val subtitle : String? = ""

    @SerializedName("post_url")
    @Expose
    internal val postUrl : String? = ""

    @SerializedName("image_url")
    @Expose
    internal val imageUrl : String? = ""

    @SerializedName("created_date")
    @Expose
    internal val createdDate : String? = ""

    @SerializedName("start_date")
    @Expose
    internal val startDate : String? = ""

    @SerializedName("expire_date")
    @Expose
    internal val expireDate : String? = ""


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
                clubCode + ""
    }

    fun getResults(): Any {
        return true
    }
}