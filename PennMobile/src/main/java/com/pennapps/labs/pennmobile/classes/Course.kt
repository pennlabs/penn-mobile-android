package com.pennapps.labs.pennmobile.classes


import com.google.gson.annotations.SerializedName

data class Course(@SerializedName("difficulty")
                  val difficulty: Double = 0.0,
                  @SerializedName("course_quality")
                  val courseQuality: Double = 0.0,
                  @SerializedName("num_sections")
                  val numSections: Int = 1,
                  @SerializedName("work_required")
                  val workRequired: Double = 0.0,
                  @SerializedName("description")
                  val description: String = "",
                  @SerializedName("recommendation_score")
                  val recommendationScore: Double = 0.0,
                  @SerializedName("semester")
                  val semester: String = "",
                  @SerializedName("id")
                  val id: String = "",
                  @SerializedName("title")
                  val title: String = "",
                  @SerializedName("instructor_quality")
                  val instructorQuality: Double = 0.0) {
    override fun toString(): String {
        return this.id
    }
}


