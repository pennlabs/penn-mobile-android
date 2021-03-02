package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jackie on 2018-03-28. Updated by Marta on 2019-10-20.
 */

class HomeCellInfo {

    // News / Feature / Post
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

    // News
    @SerializedName("article_url")
    @Expose
    val articleUrl: String? = null

    // Dining
    @SerializedName("venues")
    @Expose
    val venues: List<Int>? = null

    // Laundry
    @SerializedName("room_id")
    @Expose
    val roomId: Int? = 0

    // Courses
    @SerializedName("courses")
    @Expose
    val courses: List<HomeCourse>? = null
    @SerializedName("weekday")
    @Expose
    val weekday: String? = null

    // Feature
    // Can have source, title, description, timestamp, imageUrl, feature string
    @SerializedName("description")
    var description: String? = null
    @SerializedName("feature")
    var featureStr: String? = null

    // Post
    // Can also have source, title, subtitle, imageUrl
    // All posts must have at least an image, an id, and a test flag
    // Rules:
    //  (1) A time label cannot exist without a source label
    //  (2) An image cannot be accompanied with only a source label
    //  (3) A subtitle cannot exist without a title

    @SerializedName("time_label")
    var timeLabel: String? = null
    @SerializedName("post_url")
    var postUrl: String? = null
    @SerializedName("post_id")
    var postId: Int? = null
    @SerializedName("test")
    var isTest: Boolean? = null

}
