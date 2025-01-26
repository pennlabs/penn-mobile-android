package com.pennapps.labs.pennmobile.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationAPI {
    @POST("user/notifications/tokens/android/{token}/")
    suspend fun sendNotificationToken(
        @Header("Authorization") bearerToken: String,
        @Path("token") token: String,
    ): Response<ResponseBody>

    @DELETE("user/notifications/tokens/android/{token}/")
    suspend fun deleteNotificationToken(
        @Path("token") token: String,
    ): Response<ResponseBody>
}
