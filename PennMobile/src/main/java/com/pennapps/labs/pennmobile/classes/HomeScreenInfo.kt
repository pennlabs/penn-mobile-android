package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jackie on 2018-03-28. Updated by Marta on 2019-10-20.
 */

class HomeScreenInfo {

    // news
    @SerializedName("article_url")
    @Expose
    val articleUrl: String? = null
    @SerializedName("image_url")
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

    // dining
    @SerializedName("venues")
    @Expose
    val venues: List<Int>? = null

    // laundry
    @SerializedName("room_id")
    @Expose
    val roomId: Int? = 0

    // courses
    @SerializedName("courses")
    @Expose
    val courses: List<Course>? = null
    @SerializedName("weekday")
    @Expose
    val weekday: String? = null

    // reservations, university events are arrays


}
