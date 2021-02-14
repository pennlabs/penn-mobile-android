package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

class DiningPlan {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("start_date")
    var startDate: String? = null
    @SerializedName("end_date")
    var endDate: Int? = null
    @SerializedName("signup_date")
    var signupDate: Int? = null
    @SerializedName("cost")
    var cost: Int? = null
    @SerializedName("dining_dollars")
    var diningDollars: Int? = null
    @SerializedName("total_visits")
    var totalVisits: Int? = null
}