package com.pennapps.labs.pennmobile.classes

import android.util.Log
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Rohan Chhaya, Fall 2022.
 * Data model for custom posts on homepage
 */
class Post {
    companion object {
        const val DRAFT = "DRAFT"
    }

    @SerializedName("id")
    @Expose
    internal val id : Int? = null

    @SerializedName("club_code")
    @Expose
    internal val clubCode : String? = null

    @SerializedName("title")
    @Expose
    internal val title : String? = null

    @SerializedName("subtitle")
    @Expose
    internal val subtitle : String? = null

    @SerializedName("post_url")
    @Expose
    internal val postUrl : String? = null

    @SerializedName("image_url")
    @Expose
    internal val imageUrl : String? = null

    @SerializedName("created_date")
    @Expose
    internal val createdDate : String? = null

    @SerializedName("start_date")
    @Expose
    internal val startDate : String? = null

    @SerializedName("expire_date")
    @Expose
    internal val expireDate : String? = null

    @SerializedName("club_comment")
    @Expose
    internal val club_comment : String? = null

    @SerializedName("admin_comment")
    @Expose
    internal val admin_comment : String? = null

    @SerializedName("status")
    @Expose
    internal val status : String? = null

    @SerializedName("target_populations")
    @Expose
    internal val target_populations : List<Int>? = null

    override fun toString(): String {
        return id.toString() + ", " +
                clubCode + ""
    }
    override fun equals(other: Any?) : Boolean {
        // note: target_populations is not included because it's unused and structural
        return when (other) {
            is Post -> {
                this.id == other.id &&
                        this.clubCode == other.clubCode &&
                        this.title == other.title &&
                        this.subtitle == other.subtitle &&
                        this.postUrl == other.postUrl &&
                        this.imageUrl == other.imageUrl &&
                        this.createdDate == other.createdDate &&
                        this.startDate == other.startDate &&
                        this.expireDate == other.expireDate &&
                        this.club_comment == other.club_comment &&
                        this.admin_comment == other.admin_comment &&
                        this.status == other.status &&
                        this.target_populations == other.target_populations
            }
            else -> false
        }
    }

    override fun hashCode() : Int {
        // lazy hash function but we don't use this method anyways

        val idHash = id.hashCode().toString()
        val clubCodeHash = clubCode.hashCode().toString()
        val titleHash = title.hashCode().toString()
        val subtitleHash = subtitle.hashCode().toString()
        val postUrlHash = postUrl.hashCode().toString()
        val imageUrlHash = imageUrl.hashCode().toString()
        val createdDateHash = createdDate.hashCode().toString()
        val startDateHash = startDate.hashCode().toString()
        val expireDateHash = expireDate.hashCode().toString()
        val clubCommentHash = club_comment.hashCode().toString()
        val adminCommentHash = admin_comment.hashCode().toString()
        val statusHash = status.hashCode().toString()
        val targetPopulationsHash = target_populations.hashCode().toString()

        return (idHash + clubCodeHash + titleHash +
                subtitleHash + postUrlHash + imageUrlHash +
                createdDateHash + startDateHash + expireDateHash +
                clubCommentHash + adminCommentHash + statusHash +
                targetPopulationsHash).hashCode()
    }
}