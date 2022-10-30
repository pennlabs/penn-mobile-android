package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.classes.CampusExpressAccessTokenResponse
import com.pennapps.labs.pennmobile.classes.DiningBalances
import com.pennapps.labs.pennmobile.classes.GetUserResponse
import retrofit.Callback
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.Header
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Query
import rx.Observable

/**
 * Created by Julius Snipes on 09/23/2022.
 * Retrofit interface to the Campus Express API
 */
interface CampusExpress {

    @GET("/oauth/token")
    fun getAccessToken(
        @Query("code") authCode: String?,
        @Query("grant_type") grantType: String?,
        @Query("client_id") clientID: String?,
        @Query("redirect_uri") redirectURI: String?,
        @Query("code_verifier") codeVerifier: String?,
        callback: Callback<CampusExpressAccessTokenResponse>
    )

    @GET("/dining/currentBalance")
    fun getCurrentDiningBalances(
        @Header("x-authorization") bearerToken: String?) : Observable<DiningBalances>

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    fun getUser(
        @Header("Authorization") authorizationHeader: String?,
        @Field("token") token: String?,
        callback: Callback<GetUserResponse?>?
    )
}