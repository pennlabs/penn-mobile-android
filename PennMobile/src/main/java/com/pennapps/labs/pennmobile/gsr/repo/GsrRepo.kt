package com.pennapps.labs.pennmobile.gsr.repo

import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation

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

    suspend fun cancelGsr(
        bookingId: String?,
        isHuntsmanReservation: Boolean,
    )

    suspend fun getReservations(): List<GSRReservation>
}
