package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.pennapps.labs.pennmobile.adapters.HomeAdapter

class Poll {

    @SerializedName("id")
    @Expose
    private val id : Int? = null

    @SerializedName("club_code")
    @Expose
    val clubCode : String? = null

    @SerializedName("question")
    @Expose
    val question : String? = null

    @SerializedName("created_date")
    @Expose
    private val createdDate : String? = null

    @SerializedName("start_date")
    @Expose
    private val startDate : String? = null

    @SerializedName("expire_date")
    @Expose
    private val expireDate : String? = null

    @SerializedName("multiselect")
    @Expose
    private val multiselect : Boolean = false

    @SerializedName("user_comment")
    @Expose
    private val clubComment : String? = null

    @SerializedName("options")
    @Expose
    val options : List<PollOption> = ArrayList()

    @Expose
    var totalVotes : Int = 0

    @Expose
    var isVisible : Boolean = false

    //@Expose
    //var homeAdapter : HomeAdapter? = null

    fun selectOption(pollOption: PollOption) {
        options.forEach { if(pollOption.id == it.id) {
            if(it.selected) {
                it.voteCount = it.voteCount - 1
                totalVotes -= 1
            } else {
                it.voteCount = it.voteCount + 1
                totalVotes += 1
            }
            it.selected = !it.selected
        } else {
            if(!multiselect) {
                if(it.selected) {
                    it.voteCount = it.voteCount - 1
                    totalVotes -= 1
                }
                it.selected = false
            }
        }}
        //gui?.notifyDataSetChanged()
    }

    // Device id + poll id -> hash -> id

}