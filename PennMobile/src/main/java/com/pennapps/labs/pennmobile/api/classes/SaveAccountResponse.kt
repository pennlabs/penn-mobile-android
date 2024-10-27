package com.pennapps.labs.pennmobile.api.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveAccountResponse {
    @SerializedName("account_id")
    @Expose
    var accountID: String? = null
}
