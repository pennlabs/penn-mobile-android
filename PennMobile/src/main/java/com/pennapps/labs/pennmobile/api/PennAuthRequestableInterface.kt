package com.pennapps.labs.pennmobile.api


import retrofit2.Call
import retrofit2.http.*


/**
 * Created by Marta on 3/10/2020.
 */

interface PennAuthRequestableInterface {

    @GET
    fun makeAuthRequest(@Url url: String): Call<String>

    @FormUrlEncoded
    @POST
    fun makeRequestWithAuth(
            @Url url: String,
            @Field("j_username") pennkey: String,
            @Field("j_password") password: String,
            @Field("_eventId_proceed") proceedString: String): Call<String>

    @FormUrlEncoded
    @POST
    fun makeRequestWithShibboleth(
            @Url url: String,
            @Field("RelayState") relayState: String,
            @Field("SAMLResponse") samlResponse: String): Call<String>

}