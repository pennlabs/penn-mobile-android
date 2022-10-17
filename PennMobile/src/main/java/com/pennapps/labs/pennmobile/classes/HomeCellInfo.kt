package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jackie on 2018-03-28. Updated by Marta on 2019-10-20.
 */

class HomeCellInfo {


    // Article
    @SerializedName("article")
    @Expose
    var article: Article? = null
    // News / Feature / Post
    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null
    @SerializedName("source")
    @Expose
    var source: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = null
    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    // News
    @SerializedName("article_url")
    @Expose
    val articleUrl: String? = null

    // Dining
    @SerializedName("venues")
    @Expose
    var venues: List<Int>? = null

    // Laundry
    @SerializedName("room_id")
    @Expose
    var roomId: Int? = 0

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
