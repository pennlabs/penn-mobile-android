package com.pennapps.labs.pennmobile.gsr.repo

interface GsrRepo {
    fun getSavedUserInfo(): Triple<String, String, String>

    suspend fun bookGsr(
        startTime: String?,
        endTime: String?,
        gid: Int,
        roomId: Int,
        roomName: String,
        firstName: String,
        lastName: String,
        email: String,
    )
}
