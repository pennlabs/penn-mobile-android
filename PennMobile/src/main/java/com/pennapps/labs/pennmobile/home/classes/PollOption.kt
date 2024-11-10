package com.pennapps.labs.pennmobile.home.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PollOption {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("poll")
    @Expose
    private val poll: Int? = null

    @SerializedName("choice")
    @Expose
    val choice: String? = null

    @SerializedName("vote_count")
    @Expose
    var voteCount: Int = 0

    var selected: Boolean = false

    var isVisible: Boolean = false

    override fun equals(other: Any?): Boolean =
        other is PollOption &&
            this.choice == other.choice &&
            this.id == other.id &&
            this.voteCount == other.voteCount

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (poll ?: 0)
        result = 31 * result + (choice?.hashCode() ?: 0)
        result = 31 * result + voteCount
        result = 31 * result + selected.hashCode()
        result = 31 * result + isVisible.hashCode()
        return result
    }
}
