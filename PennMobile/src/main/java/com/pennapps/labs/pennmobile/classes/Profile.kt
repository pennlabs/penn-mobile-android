package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class Profile(@SerializedName("push_notifications")
                   val pushNotifications: Boolean = false,
                   @SerializedName("phone")
                   val phone: String = "",
                   @SerializedName("email")
                   val email: String = "")