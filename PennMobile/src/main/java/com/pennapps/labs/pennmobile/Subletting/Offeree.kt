package com.pennapps.labs.pennmobile.Subletting

import com.google.gson.annotations.SerializedName

data class Offeree(@SerializedName("phone_number")
                    val phoneNumber: String = "",
                    @SerializedName("email")
                    val email: String = "",
                   @SerializedName("message")
                   val message: String = "",
                   @SerializedName("sublet")
                   val sublet: Int = 0)