package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

class DiningBalance {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("dining_dollars")
    var diningDollars: String? = null
    @SerializedName("regular_visits")
    var regularVisits: Int? = null
    @SerializedName("guest_visits")
    var guestVisits: Int? = null
    @SerializedName("add_on_visits")
    var addOnVisits: Int? = null
}