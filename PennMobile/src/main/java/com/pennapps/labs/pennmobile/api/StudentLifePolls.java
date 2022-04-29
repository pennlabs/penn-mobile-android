package com.pennapps.labs.pennmobile.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pennapps.labs.pennmobile.PollResult;
import com.pennapps.labs.pennmobile.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.classes.GSRBookingResult;
import com.pennapps.labs.pennmobile.classes.GetUserResponse;
import com.pennapps.labs.pennmobile.classes.Poll;
import com.pennapps.labs.pennmobile.classes.PollPop;
import com.pennapps.labs.pennmobile.classes.Post;
import com.pennapps.labs.pennmobile.classes.Venue;
import retrofit.client.Response;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface StudentLifePolls {

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


    @GET("/posts/browse/")
    void validPostsList(
            @Header("Authorization") String bearerToken,
            Callback<JsonArray> callback
    );

    @Headers({"Content-Type: application/json"})
    @FormUrlEncoded
    @POST("/polls/browse/")
    void validPollsList(
            @Header("Authorization") String bearerToken,
            @Field("id_hash") String id_hash,
            Callback<PollResult> callback
    );


    @GET("/polls/review/")
    Observable<List<Poll>> reviewPollsList();

    @FormUrlEncoded
    @PATCH("/polls/{id}")
    void updatePollField( //potentially return callback
            @Path("id") int id
    );

    @FormUrlEncoded
    @DELETE("/polls/{id}")
    void deletePoll(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("/options")
    void createPollOption(
            @Field("id") int id
    );

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
    void createPollVote(
            @Header("Authorization") String bearerToken,
            Callback<PollResult> callback
    );

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


    @GET("/poll-history")
    Observable<List<Poll>> pollsHistory();


    @GET("/populations")
    Observable<List<PollPop>> pollsPopulations(
            @Header("Authorization") String bearerToken
    );

    @FormUrlEncoded
    @GET("/vote-statistics/{id}")
    Observable<String> getVoteStats(
            @Path("id") int id
    );
}

