package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiningBalancesList {
    @SerializedName("balance_list")
    @Expose
    var diningBalancesList: List<DiningBalances>? = null
}
