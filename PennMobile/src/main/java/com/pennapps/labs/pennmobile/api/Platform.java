package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.classes.GetUserResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

public interface Platform {

    String platformBaseUrl = "https://platform.pennlabs.org";
    String campusExpressBaseUrl = "https://prod.campusexpress.upenn.edu/api/v1";

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    void getUser(
            @Header("Authorization") String authorizationHeader,
            @Field("token") String token,
            Callback<GetUserResponse> callback);

}
