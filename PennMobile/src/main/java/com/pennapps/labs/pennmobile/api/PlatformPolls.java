package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.classes.GetUserResponse;

import org.apache.commons.lang3.RandomStringUtils;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;

public interface PlatformPolls {

    String platformBaseUrl = "https://studentlife.pennlabs.org/portal/";

    @FormUrlEncoded
    @POST("/polls")
    void createPoll(
            /*@Field("code") String authCode,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            @Field("redirect_uri") String redirectURI,
            @Field("code_verifier") String codeVerifier,
            Callback<AccessTokenResponse> callback*/);

    @FormUrlEncoded
    @GET("/polls/browse/")
    void browsePolls(
            /*@Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientID,
            Callback<AccessTokenResponse> callback*/);

    @FormUrlEncoded
    @GET("/polls/review/")
    void reviewPolls(
            /*@Header("Authorization") String authorizationHeader,
            @Field("token") String token,
            Callback<GetUserResponse> callback*/);

    @FormUrlEncoded
    @PATCH("/polls/{id}")
    void updatePollField(
            @Path("id") int id
    );

    @FormUrlEncoded
    @DELETE("/polls/{id}")
    void deletePoll(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("/options")
    void createPollOption();

    @FormUrlEncoded
    @PATCH("/options/{id}")
    void updatePollOptionField(
            @Path("id") int id
    );

    @FormUrlEncoded
    @DELETE("/options/{id}")
    void deletePollOption(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("/votes")
    void createPollVote();

    @FormUrlEncoded
    @PATCH("/votes/{id}")
    void updateVoteField(
            @Path("id") int id
    );

    @FormUrlEncoded
    @DELETE("/votes/{id}")
    void deleteVote(
            @Path("id") int id
    );

    @FormUrlEncoded
    @GET("/poll-history")
    void getPollHistory();

    @FormUrlEncoded
    @GET("/populations")
    void getPopulationTypes();

    @FormUrlEncoded
    @GET("/vote-statistics/{id}")
    void getVoteStats(
            @Path("id") int id
    );
}


