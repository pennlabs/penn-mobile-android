package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.api.classes.Account;
import com.pennapps.labs.pennmobile.dining.classes.DiningHall;
import com.pennapps.labs.pennmobile.dining.classes.DiningRequest;
import com.pennapps.labs.pennmobile.fling.classes.FlingEvent;
import com.pennapps.labs.pennmobile.gsr.classes.GSRBookingResult;
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation;
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation;
import com.pennapps.labs.pennmobile.home.classes.Poll;
import com.pennapps.labs.pennmobile.home.classes.Post;
import com.pennapps.labs.pennmobile.api.classes.SaveAccountResponse;
import com.pennapps.labs.pennmobile.dining.classes.Venue;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Julius.
 * Retrofit interface to the Penn Mobile API
 */
public interface StudentLife {

    @GET("/dining/venues")
    Observable<List<Venue>> venues();

    @GET("/dining/menus/{day}")
    Observable<List<DiningHall.Menu>> getMenus(
            @Path("day") String day);

    @GET("/dining/weekly_menu/{id}")
    Observable<DiningHall> daily_menu(
            @Path("id") int id);

    @GET("/gsr/locations")
    Observable<List<GSRLocation>> location ();

    @FormUrlEncoded
    @POST("/gsr/book/")
    void bookGSR(
            @Header("Authorization") String bearerToken,
            @Field("start_time") String start,
            @Field("end_time") String end,
            @Field("gid") int gid,
            @Field("id") int id,
            @Field("room_name") String roomName,
            Callback<GSRBookingResult> callback);

    @GET("/events/fling")
    Observable<List<FlingEvent>> getFlingEvents();

    @GET("/gsr/reservations")
    Observable<List<GSRReservation>> getGsrReservations(
            @Header("Authorization") String bearerToken
    );

    @FormUrlEncoded
    @POST("/gsr/cancel/")
    void cancelReservation(
            @Header("Authorization") String bearerToken,
            @Header("X-Device-ID") String deviceID,
            @Field("booking_id") String bookingID,
            @Field("sessionid") String sessionID,
            Callback<Response> callback);

    // accounts
    @Headers({"Content-Type: application/json"})
    @POST("/users/{pennkey}/activate/")
    void saveAccount(
            @Header("Authorization") String bearerToken,
            @Path("pennkey") String pennkey,
            @Body Account account,
            Callback<SaveAccountResponse> callback);

    @GET("/laundry/preferences")
    Observable<List<Integer>> getLaundryPref(
            @Header("Authorization") String bearerToken);

    @Headers({"Content-Type: application/json"})
    @POST("/dining/preferences/")
    void sendDiningPref(
            @Header("Authorization") String bearerToken,
            @Body DiningRequest body,
            Callback<Response> callback);

    @GET("/portal/posts/browse/")
    Observable<List<Post>> validPostsList(
            @Header("Authorization") String bearerToken
    );

    @FormUrlEncoded
    @POST("/portal/polls/browse/")
    Observable<List<Poll>> browsePolls(
            @Header("Authorization") String bearerToken,
            @Field("id_hash") String idHash
    );

    @FormUrlEncoded
    @POST("/portal/votes/")
    void createPollVote(
            @Header("Authorization") String bearerToken,
            @Field("id_hash") String idHash,
            @Field("poll_options") ArrayList<Integer> pollOptions,
            Callback<Response> callback);
}
