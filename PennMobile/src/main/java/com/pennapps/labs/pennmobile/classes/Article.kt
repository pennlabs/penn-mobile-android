package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Julius on 2022-04-16.
 */

class Article {
    @SerializedName("imageurl")
    @Expose
    val imageUrl: String? = null
    @SerializedName("source")
    @Expose
    val source: String? = null
    @SerializedName("title")
    @Expose
    val title: String? = null
    @SerializedName("subtitle")
    @Expose
    val subtitle: String? = null
    @SerializedName("timestamp")
    @Expose
    val timestamp: String? = null

    // News
    @SerializedName("link")
    @Expose
    val articleUrl: String? = null
}