package com.pennapps.labs.pennmobile.api;

import android.provider.Settings;
import android.util.Log;

import com.google.gson.JsonObject;
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.GetUserResponse;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.util.List;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface Platform {

    String platformBaseUrl = "https://platform.pennlabs.org";
    String codeVerifier = RandomStringUtils.randomAlphanumeric(64);

    @FormUrlEncoded
    @POST("/accounts/token/")
    void getAccessToken(
            @Field("code") String authCode,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            @Field("redirect_uri") String redirectURI,
            @Field("code_verifier") String codeVerifier,
            Callback<AccessTokenResponse> callback);

    @FormUrlEncoded
    @POST("/accounts/token/")
    void refreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            Callback<AccessTokenResponse> callback);

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    void getUser(
            @Header("Authorization") String authorizationHeader,
            @Field("token") String token,
            Callback<GetUserResponse> callback);

}
