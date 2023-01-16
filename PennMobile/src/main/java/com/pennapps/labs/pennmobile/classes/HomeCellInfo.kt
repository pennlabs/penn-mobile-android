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
    // Is defined of type 'Post' and has source, imageUrl,
    // postUrl, title, subtitle, start/end times, and comments
    //NOTE: Only the most recent post from the API will be processed
    @SerializedName("time_label")
    var post: Post? = null
    @SerializedName("test")
    val isTest: Boolean? = null



}
