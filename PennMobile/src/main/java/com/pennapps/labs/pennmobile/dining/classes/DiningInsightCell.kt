package com.pennapps.labs.pennmobile.dining.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class DiningInsightCell {
    @SerializedName("type")
    @Expose
    var type: String? = null

    var diningBalances: DiningBalances? = null

    var diningBalancesList: DiningBalancesList? = null
}
