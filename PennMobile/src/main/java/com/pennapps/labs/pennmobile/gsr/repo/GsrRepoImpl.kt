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
            // 1. Get Token (Throw if null so ViewModel catches it)
            val accessToken =
                oAuth2NetworkManager.getAccessToken()
                    ?: throw Exception("Authentication failed. Please log in again.")

            val bearerToken = "Bearer $accessToken"

            // 2. Perform Network Call
            val response =
                studentLife.bookGSR(
                    bearerToken,
                    startTime,
                    endTime,
                    gid,
                    roomId,
                    roomName,
                )

            // 3. Handle Response
            if (response.isSuccessful) {
                val result = response.body()
                if (result?.getDetail() == "success") {
                    // Success: Save user info and return (implicitly returns Unit)
                    saveUserInfo(firstName, lastName, email)
                } else {
                    // API-level error (e.g., room already taken)
                    val errorMsg = result?.getError() ?: "GSR booking failed."
                    throw Exception(errorMsg)
                }
            } else {
                // HTTP-level error (e.g., 404, 500)
                val errorBody = response.errorBody()?.string()
                Log.e("GsrRepoImpl", "HTTP Error: $errorBody")
                throw Exception("Server returned an error. Please try again.")
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
