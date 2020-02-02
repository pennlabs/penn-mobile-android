package com.pennapps.labs.pennmobile.api;

import android.util.Log;

import com.pennapps.labs.pennmobile.classes.Course;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.util.List;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface Platform {

    String baseUrl = "https://platform.pennlabs.org";
    String clientID = "CJmaheeaQ5bJhRL0xxlxK3b8VEbLb3dMfUAvI2TN";
    String redirectUri = "https%3A%2F%2Fpennlabs.org%2Fpennmobile%2Fios%2Fcallback%2F";
    String codeVerifier = RandomStringUtils.randomAlphanumeric(64);

    @FormUrlEncoded
    @POST("/accounts/token/")
    void getAccessToken(
            @Field("code") String authCode,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            @Field("redirect_uri") String redirectURI,
            @Field("code_verifier") String codeVerifier,
            Callback<Response> callback);

    // response
//    if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200, let data = data {
//        let json = JSON(data)
//        let expiresIn = json["expires_in"].intValue
//        let expiration = Date().add(seconds: expiresIn)
//        let accessToken = AccessToken(value: json["access_token"].stringValue, expiration: expiration)
//        let refreshToken = json["refresh_token"].stringValue
//        self.saveRefreshToken(token: refreshToken)
//        self.currentAccessToken = accessToken
//        callback(accessToken)
//        return
//    }

    @FormUrlEncoded
    @POST("/accounts/token/")
    void refreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    void getUser(
            @Field("token") String token,
            Callback<Response> callback);

}
