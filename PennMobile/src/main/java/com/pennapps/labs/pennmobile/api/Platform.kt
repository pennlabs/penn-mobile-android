package com.pennapps.labs.pennmobile.api
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse
import com.pennapps.labs.pennmobile.classes.GetUserResponse
import org.apache.commons.lang3.RandomStringUtils
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.Header
import retrofit.http.POST

interface Platform {

    @FormUrlEncoded
    @POST("/accounts/token/")
    fun getAccessToken(
            @Field("code") authCode: String,
            @Field("grant_type") grantType: String,
            @Field("client_id") clientID: String,
            @Field("redirect_uri") redirectURI: String,
            @Field("code_verifier") codeVerifier: String,
            callback: Callback<AccessTokenResponse>)

    @FormUrlEncoded
    @POST("/accounts/token/")
    fun refreshAccessToken(
            @Field("refresh_token") refreshToken: String,
            @Field("grant_type") grantType: String,
            @Field("client_id") clientID: String,
            callback: Callback<AccessTokenResponse>)

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    fun getUser(
            @Header("Authorization") authorizationHeader: String,
            @Field("token") token: String,
            callback: Callback<GetUserResponse>)

    companion object {
        const val platformBaseUrl : String = "https://platform.pennlabs.org"
        val codeVerifier : String = RandomStringUtils.randomAlphanumeric(64)
    }

}
