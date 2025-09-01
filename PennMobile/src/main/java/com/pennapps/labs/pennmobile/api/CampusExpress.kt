package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.api.classes.CampusExpressAccessTokenResponse
import com.pennapps.labs.pennmobile.dining.classes.DiningBalances
import com.pennapps.labs.pennmobile.dining.classes.DiningBalancesList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by Julius Snipes on 09/23/2022.
 * Retrofit interface to the Campus Express API
 */
interface CampusExpress {
    @GET("oauth/token")
    suspend fun getAccessToken(
        @Query("code") authCode: String?,
        @Query("grant_type") grantType: String?,
        @Query("client_id") clientID: String?,
        @Query("redirect_uri") redirectURI: String?,
        @Query("code_verifier") codeVerifier: String?,
    ): Response<CampusExpressAccessTokenResponse>

    @GET("dining/currentBalance")
    suspend fun getCurrentDiningBalances(
        @Header("x-authorization") bearerToken: String?,
    ): DiningBalances

    @GET("dining/pastBalances")
    suspend fun getPastDiningBalances(
        @Header("x-authorization") bearerToken: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): DiningBalancesList
}
