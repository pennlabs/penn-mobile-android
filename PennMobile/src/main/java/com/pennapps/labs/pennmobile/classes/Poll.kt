package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Poll {
    companion object {
        const val DRAFT = "DRAFT"
    }

    @SerializedName("id")
    @Expose
    private val id: Int? = null

    @SerializedName("club_code")
    @Expose
    val clubCode: String? = null

    @SerializedName("question")
    @Expose
    val question: String? = null

    @SerializedName("created_date")
    @Expose
    private val createdDate: String? = null

    @SerializedName("start_date")
    @Expose
    private val startDate: String? = null

    @SerializedName("expire_date")
    @Expose
    private val expireDate: String? = null

    @SerializedName("multiselect")
    @Expose
    private val multiselect: Boolean = false

    @SerializedName("user_comment")
    @Expose
    private val clubComment: String? = null

    @SerializedName("options")
    @Expose
    val options: List<PollOption> = ArrayList()

    @SerializedName("status")
    @Expose
    internal val status: String? = null

    @Expose
    var totalVotes: Int = 0

    @Expose
    var isVisible: Boolean = false

    override fun equals(other: Any?): Boolean {
        return other is Poll && this.id == other.id && this.totalVotes == other.totalVotes &&
            this.question == other.question && this.options.size == other.options.size &&
            this.options.containsAll(other.options) && other.options.containsAll(this.options)
    }

    // @Expose
    // var homeAdapter : HomeAdapter? = null

    fun selectOption(pollOption: PollOption) {
        options.forEach {
            if (pollOption.id == it.id) {
                if (it.selected) {
                    it.voteCount = it.voteCount - 1
                    totalVotes -= 1
                } else {
                    it.voteCount = it.voteCount + 1
                    totalVotes += 1
                }
                it.selected = !it.selected
            } else {
                if (!multiselect) {
                    if (it.selected) {
                        it.voteCount = it.voteCount - 1
                        totalVotes -= 1
                    }
                    it.selected = false
                }
            }
        }
        // gui?.notifyDataSetChanged()
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (clubCode?.hashCode() ?: 0)
        result = 31 * result + (question?.hashCode() ?: 0)
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (expireDate?.hashCode() ?: 0)
        result = 31 * result + multiselect.hashCode()
        result = 31 * result + (clubComment?.hashCode() ?: 0)
        result = 31 * result + options.hashCode()
        result = 31 * result + totalVotes
        result = 31 * result + isVisible.hashCode()
        return result
    }

    // Device id + poll id -> hash -> id
}
