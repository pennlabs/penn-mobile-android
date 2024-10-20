package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class DiningBalances {
    @SerializedName("dining_dollars")
    @Expose
    var diningDollars: String? = null

    @SerializedName("regular_visits")
    @Expose
    var regularVisits: Int? = null

    @SerializedName("guest_visits")
    @Expose
    var guestVisits: Int? = null

    @SerializedName("date")
    @Expose
    var date: String? = null
}
