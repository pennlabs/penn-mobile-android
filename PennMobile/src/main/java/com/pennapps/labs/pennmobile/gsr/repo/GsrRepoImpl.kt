package com.pennapps.labs.pennmobile.gsr.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.JsonParser
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.OAuth2NetworkManager
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.api.classes.AccessToken
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.gsr.domain.GsrApiException
import com.pennapps.labs.pennmobile.gsr.domain.GsrNotLoggedInException
import com.pennapps.labs.pennmobile.gsr.domain.GsrOfflineException
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrRoom
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

        override suspend fun getLocations(): List<GsrLocation> {
            val bearerToken = bearerToken()
            val response = call { studentLife.getGsrLocations(bearerToken) }

            if (!response.isSuccessful) {
                throw response.apiError("Error fetching GSR locations.")
            }
            return response.body().orEmpty().mapNotNull { it.toDomain() }
        }

        override suspend fun getAvailability(
            location: GsrLocation,
            startDate: LocalDate,
            endDate: LocalDate,
        ): List<GsrRoom> {
            val bearerToken = bearerToken()
            val start = DateTimeFormatter.ISO_LOCAL_DATE.format(startDate)
            val end = DateTimeFormatter.ISO_LOCAL_DATE.format(endDate).takeIf { endDate != startDate }
            val response = call { studentLife.getGsrAvailability(bearerToken, location.lid, location.gid, start, end) }

            if (!response.isSuccessful) {
                throw response.apiError("Error fetching GSR availability.")
            }

            return response
                .body()
                ?.rooms
                .orEmpty()
                .mapNotNull { it.toDomain() }
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
            val bearerToken = bearerToken()

            val response =
                call {
                    studentLife.bookGSR(
                        bearerToken,
                        startTime,
                        endTime,
                        gid,
                        roomId,
                        roomName,
                    )
                }

            if (response.isSuccessful) {
                val result = response.body()
                if (result?.getDetail() == "success") {
                    saveUserInfo(firstName, lastName, email)
                } else {
                    val errorMsg = result?.getError() ?: "GSR booking failed."
                    throw Exception(errorMsg)
                }
            } else {
                throw response.apiError("Server returned an error. Please try again.")
            }
        }

        override suspend fun cancelGsr(
            bookingId: String?,
            isHuntsmanReservation: Boolean,
        ) {
            val bearerToken = bearerToken()

            val sessionId =
                if (isHuntsmanReservation) {
                    sharedPreferences.getString(context.getString(R.string.huntsmanGSR_SessionID), "")
                } else {
                    null
                }

            val response =
                call {
                    studentLife.cancelReservation(
                        bearerToken,
                        null,
                        bookingId,
                        sessionId,
                    )
                }

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("GsrRepoImpl", "HTTP Error: $errorBody")
                throw Exception("Error deleting your GSR reservation.")
            }
        }

        override suspend fun getReservations(): List<GSRReservation> {
            val bearerToken = bearerToken()
            val response = call { studentLife.getGsrReservations(bearerToken) }

            if (response.isSuccessful) {
                return response.body() ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("GsrRepoImpl", "HTTP Error fetching reservations: $errorBody")
                throw Exception("Error fetching reservations.")
            }
        }

        private suspend fun bearerToken(): String =
            when (val token = oAuth2NetworkManager.getAccessTokenResult()) {
                is AccessToken.Valid -> "Bearer ${token.token}"
                AccessToken.Guest, AccessToken.LoggedOut -> throw GsrNotLoggedInException()
                AccessToken.NetworkUnavailable -> throw GsrOfflineException()
            }

        private suspend fun <T> call(request: suspend () -> Response<T>): Response<T> =
            try {
                request()
            } catch (e: IOException) {
                throw GsrOfflineException(e)
            }

        /**
         * The backend reports failures as HTTP 400 `{"error": "<reason>"}`; surface that reason
         * instead of a generic message so users see e.g. "Error: Not enough credits".
         */
        private fun Response<*>.apiError(fallback: String): Exception {
            val errorBody = errorBody()?.string()
            Log.e("GsrRepoImpl", "HTTP ${code()}: $errorBody")
            val reason =
                try {
                    JsonParser
                        .parseString(errorBody.orEmpty())
                        .takeIf { it.isJsonObject }
                        ?.asJsonObject
                        ?.get("error")
                        ?.takeIf { it.isJsonPrimitive }
                        ?.asString
                } catch (e: Exception) {
                    null
                }
            return if (reason.isNullOrBlank()) Exception(fallback) else GsrApiException(reason)
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
