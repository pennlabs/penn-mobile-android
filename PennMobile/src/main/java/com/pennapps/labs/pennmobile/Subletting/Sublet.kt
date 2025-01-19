package com.pennapps.labs.pennmobile.Subletting

import com.google.gson.annotations.SerializedName

data class Sublet(@SerializedName("end_date")
                  val endDate: String = "",
                  @SerializedName("amenities")
                  val amenities: List<String>? = null,
                  @SerializedName("baths")
                  val baths: Int? = 0,
                  @SerializedName("address")
                  val address: String? = "",
                  @SerializedName("price")
                  val price: Int = 0,
                  @SerializedName("expires_at")
                  val expiresAt: String = "",
                  @SerializedName("description")
                  val description: String? = "",
                  @SerializedName("title")
                  val title: String = "",
                  @SerializedName("beds")
                  val beds: Int? = 0,
                  @SerializedName("external_link")
                  val externalLink: String = "",
                  @SerializedName("start_date")
                  val startDate: String = "",
                  @SerializedName("id")
                  val id: Int? = null,
                  @SerializedName("subletter")
                  val subletter: String? = null,
                  @SerializedName("created_at")
                  val createdAt: String? = null,
                  @SerializedName("images")
                  val images: List<SubletImage>? = null
        )

data class SubletImage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
)