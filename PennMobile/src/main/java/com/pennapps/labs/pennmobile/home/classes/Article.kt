package com.pennapps.labs.pennmobile.home.classes

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

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Article -> {
                this.imageUrl == other.imageUrl &&
                    this.source == other.source &&
                    this.title == other.title &&
                    this.subtitle == other.subtitle &&
                    this.timestamp == other.timestamp &&
                    this.articleUrl == other.articleUrl
            } else -> false
        }

    override fun hashCode(): Int {
        // lazy hash function but we don't use this method anyways
        val urlHash = imageUrl.hashCode().toString()
        val sourceHash = source.hashCode().toString()
        val titleHash = title.hashCode().toString()
        val subtitleHash = subtitle.hashCode().toString()
        val timeHash = timestamp.hashCode().toString()
        val articleHash = articleUrl.hashCode().toString()

        return (urlHash + sourceHash + titleHash + subtitleHash + timeHash + articleHash).hashCode()
    }
}
