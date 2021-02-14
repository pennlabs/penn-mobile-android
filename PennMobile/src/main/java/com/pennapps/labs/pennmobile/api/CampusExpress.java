package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.AccessToken;
import com.pennapps.labs.pennmobile.classes.DiningBalance;
import com.pennapps.labs.pennmobile.classes.DiningPlan;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Header;

/**
 * Created by Adel on 12/13/14.
 * Retrofit interface to the Penn Labs API
 */
public interface CampusExpress {

    // TODO: swap for final URL once the API is finalized
    String campusExpressBaseUrl = "https://prod.campusexpress.upenn.edu/api/v1_demo/";

    @GET("/oauth/authorize")
    void authorize(
            @Field("response_type") String responseType,
            @Field("client_id") String clientID,
            @Field("state") String state,
            @Field("code_challenge") String codeChallenge,
            @Field("code_challenge_method") String codeChallengeMethod,
            @Field("redirect_uri") String redirectUri,
            @Field("scope") String scope,
            Callback<AccessToken> callback);

    @GET("/oauth/token")
    void getAccessToken(
            @Field("client_id") String clientID,
            @Field("code_verifiier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            Callback<AccessToken> callback);

    @GET("/dining/currentBalance")
    DiningBalance getCurrentBalance(
            @Header("X-Authorization") String xAuthorization);

    @GET("/dining/currentPlan")
    DiningPlan getCurrentPlan(
            @Header("X-Authorization") String xAuthorization);
}
