package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Course;

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

    @GET("/accounts/authorize?XXX")
    Observable<List<Course>> authorize(
            @Query("q") String name);

    @GET("/accounts/authorize/?response_type=code&client_id=" +
            "CJmaheeaQ5bJhRL0xxlxK3b8VEbLb3dMfUAvI2TN&redirect_uri=https%3A%2F%2Fpennlabs.org%" +
            "2Fpennmobile%2Fios%2Fcallback%2F&code_challenge_method=S256" +
            "&scope=read+introspection&state=")
    Observable<List<Course>> login();

    @FormUrlEncoded
    @POST("/accounts/token/?grant_type=authorization_code")
    void getAccessToken(
            @Field("code") String authCode,
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
    @POST("/accounts/token/?grant_type=refresh_token")
    void refreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("client_id") String clientID,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/accounts/introspect/")
    void getUser(
            @Field("token") String token,
            Callback<Response> callback);

}
