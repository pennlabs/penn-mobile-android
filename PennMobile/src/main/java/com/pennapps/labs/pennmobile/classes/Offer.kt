package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class Offer(@SerializedName("sublet")
                 val sublet: Int = 0,
                 @SerializedName("phone_number")
                 val phoneNumber: String = "",
                 @SerializedName("id")
                 val id: Int = 0,
                 @SerializedName("created_date")
                 val createdDate: String = "",
                 @SerializedName("message")
                 val message: String = "",
                 @SerializedName("user")
                 val user: String = "",
                 @SerializedName("email")
                 val email: String = "")