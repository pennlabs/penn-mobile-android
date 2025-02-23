package com.pennapps.labs.pennmobile.Subletting

import com.google.gson.annotations.SerializedName

data class Offeree(@SerializedName("phone_number")
                    val phoneNumber: String = "+18000000000", // format
                    @SerializedName("email")
                    val email: String = "",
                   @SerializedName("message")
                   val message: String = "")