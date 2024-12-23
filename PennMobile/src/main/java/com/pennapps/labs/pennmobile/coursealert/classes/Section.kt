package com.pennapps.labs.pennmobile.coursealert.classes

import com.google.gson.annotations.SerializedName

data class Section(
    @SerializedName("section_id")
    val sectionId: String = "",
    @SerializedName("activity")
    val activity: String = "",
    @SerializedName("meeting_times")
    val meetingTimes: String = "",
    @SerializedName("course_code")
    val courseCode: String = "",
    @SerializedName("course_title")
    val courseTitle: String = "",
    @SerializedName("semester")
    val semester: String = "",
    @SerializedName("registration_volume")
    val registrationVolume: Int = 0,
    @SerializedName("status")
    val status: String = "",
) {
    override fun toString(): String {
        val sectionStatus =
            when (this.status) {
                "O" -> "Open"
                "C" -> "Closed"
                "X" -> "Cancelled"
                else -> "Unlisted"
            }
        return this.sectionId + " - " + sectionStatus
    }
}
