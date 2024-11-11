package com.pennapps.labs.pennmobile.fling.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FlingEvent {
    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("facebook")
    @Expose
    var facebook: String? = null

    @SerializedName("image_url")
    @Expose
    var imageUrl: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("website")
    @Expose
    var website: String? = null

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("description: ")
        if (description != null) {
            stringBuilder.append(description)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", email: ")
        if (email != null) {
            stringBuilder.append(email)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", end_time: ")
        if (endTime != null) {
            stringBuilder.append(endTime)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", facebook: ")
        if (facebook != null) {
            stringBuilder.append(facebook)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", imageUrl: ")
        if (imageUrl != null) {
            stringBuilder.append(imageUrl)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", name: ")
        if (name != null) {
            stringBuilder.append(name)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", startTime: ")
        if (startTime != null) {
            stringBuilder.append(startTime)
        } else {
            stringBuilder.append("null")
        }
        stringBuilder.append(", website: ")
        if (website != null) {
            stringBuilder.append(website)
        } else {
            stringBuilder.append("null")
        }
        return stringBuilder.toString()
    }
}
