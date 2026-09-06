package com.pennapps.labs.pennmobile.gsr.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GsrRepoImpl
    @Inject
    constructor(
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val context: Context,
        private val studentLife: StudentLife,
        private val oAuth2NetworkManager: OAuth2NetworkManager,
    ) : GsrRepo {
        override fun getSavedUserInfo(): Triple<String, String, String> {
            val firstName = sharedPreferences.getString(context.getString(R.string.first_name), "") ?: ""
            val lastName = sharedPreferences.getString(context.getString(R.string.last_name), "") ?: ""
            val email = sharedPreferences.getString(context.getString(R.string.email_address), "") ?: ""
            return Triple(firstName, lastName, email)
        }

        override suspend fun bookGsr(
            startTime: String?,
            endTime: String?,
            gid: Int,
            roomId: Int,
            roomName: String,
            firstName: String,
            lastName: String,
            email: String,
        ) {
            val accessToken =
                oAuth2NetworkManager.getAccessToken()
                    ?: throw Exception("Authentication failed. Please log in again.")

            val bearerToken = "Bearer $accessToken"

            val response =
                studentLife.bookGSR(
                    bearerToken,
                    startTime,
                    endTime,
                    gid,
                    roomId,
                    roomName,
                )

            if (response.isSuccessful) {
                val result = response.body()
                if (result?.getDetail() == "success") {
                    saveUserInfo(firstName, lastName, email)
                } else {
                    val errorMsg = result?.getError() ?: "GSR booking failed."
                    throw Exception(errorMsg)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GsrRepoImpl", "HTTP Error: $errorBody")
                throw Exception("Server returned an error. Please try again.")
            }
        }

        override suspend fun cancelGsr(
            bookingId: String?,
            sessionId: String?,
        ) {
            val accessToken =
                oAuth2NetworkManager.getAccessToken()
                    ?: throw Exception("Authentication failed. Please log in again.")

            val bearerToken = "Bearer $accessToken"

            val response =
                studentLife.cancelReservation(
                    bearerToken,
                    null,
                    bookingId,
                    sessionId,
                )

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("GsrRepoImpl", "HTTP Error: $errorBody")
                throw Exception("Error deleting your GSR reservation.")
            }
        }

        private fun saveUserInfo(
            firstName: String,
            lastName: String,
            email: String,
        ) {
            sharedPreferences.edit().apply {
                putString(context.getString(R.string.first_name), firstName)
                putString(context.getString(R.string.last_name), lastName)
                putString(context.getString(R.string.email_address), email)
                apply()
            }
        }
    }
