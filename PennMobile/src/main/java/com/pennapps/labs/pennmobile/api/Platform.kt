package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.api.classes.GetUserResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface Platform {
    companion object {
        val platformBaseUrl: String = "https://platform.pennlabs.org/"
        val campusExpressBaseUrl: String = "https://prod.campusexpress.upenn.edu/api/v1/"
    }

    @FormUrlEncoded
    @POST("accounts/introspect/")
    suspend fun getUser(
        @Header("Authorization") authorizationHeader: String?,
        @Field("token") token: String?,
    ): Response<GetUserResponse>
}
