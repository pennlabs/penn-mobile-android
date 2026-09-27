package com.pennapps.labs.pennmobile.more.classes

/** [photoUrl] is null for people with no photo on the Penn Labs website; the UI shows a placeholder. */
data class TeamMember(
    val name: String,
    val photoUrl: String?,
)
