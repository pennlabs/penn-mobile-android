package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.classes.CampusExpressAccessTokenResponse
import com.pennapps.labs.pennmobile.classes.DiningBalances
import com.pennapps.labs.pennmobile.classes.DiningBalancesList
import retrofit.Callback
import retrofit.http.GET
import retrofit.http.Header
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
        callback: Callback<CampusExpressAccessTokenResponse>,
    )

    @GET("/dining/currentBalance")
    fun getCurrentDiningBalances(
        @Header("x-authorization") bearerToken: String?,
    ): Observable<DiningBalances>

    @GET("/dining/pastBalances")
    fun getPastDiningBalances(
        @Header("x-authorization") bearerToken: String?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): Observable<DiningBalancesList>
}
