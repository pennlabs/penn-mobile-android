package com.pennapps.labs.pennmobile.api

import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.client.Response
import retrofit.http.*
import rx.Observable

/**
 * Created by Marta on 3/10/2020.
 * General interface, should create different instances for each base URL
 */

interface PennAuthRequestableInterface {

    @GET("/{target}")
    fun makeAuthRequest(@Path("target", encode = false) target: String) : Observable<Response>

    @FormUrlEncoded
    @POST("/")
    fun makeRequestWithAuth(
            @Field("j_username") pennkey: String,
            @Field("j_password") password: String,
            @Field("_eventId_proceed") proceedString: String,
            callback: Callback<Response>)

    @FormUrlEncoded
    @POST("/")
    fun makeRequestWithShibboleth(
            @Field("RelayState") relayState: String,
            @Field("SAMLResponse") samlResponse: String,
            callback: Callback<Response>)
}