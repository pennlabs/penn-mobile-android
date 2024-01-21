package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class AmenitiesItem(
        @SerializedName("amenities") val amenities: List<String>

)