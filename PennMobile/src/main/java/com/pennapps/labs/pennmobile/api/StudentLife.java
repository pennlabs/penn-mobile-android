package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.api.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.api.classes.Account;
import com.pennapps.labs.pennmobile.home.classes.Article;
import com.pennapps.labs.pennmobile.home.classes.CalendarEvent;
import com.pennapps.labs.pennmobile.dining.classes.DiningHall;
import com.pennapps.labs.pennmobile.dining.classes.DiningPreferences;
import com.pennapps.labs.pennmobile.dining.classes.DiningRequest;
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRequest;
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoom;
import com.pennapps.labs.pennmobile.fitness.classes.FitnessRoomUsage;
import com.pennapps.labs.pennmobile.fling.classes.FlingEvent;
import com.pennapps.labs.pennmobile.gsr.classes.GSR;
import com.pennapps.labs.pennmobile.gsr.classes.GSRBookingResult;
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation;
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation;
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.home.classes.Poll;
import com.pennapps.labs.pennmobile.home.classes.Post;
import com.pennapps.labs.pennmobile.api.classes.SaveAccountResponse;
import com.pennapps.labs.pennmobile.dining.classes.Venue;
import com.pennapps.labs.pennmobile.gsr.classes.WhartonStatus;

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
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Julius.
 * Retrofit interface to the Penn Mobile API
 */
public interface StudentLife {

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

    @GET("/dining/venues")
    Observable<List<Venue>> venues();

    @GET("/dining/menus/{day}")
    Observable<List<DiningHall.Menu>> getMenus(
            @Path("day") String day);

    @GET("/dining/weekly_menu/{id}")
    Observable<DiningHall> daily_menu(
            @Path("id") int id);

    @GET("/dining/preferences")
    Observable<DiningPreferences> getDiningPreferences(
            @Header("Authorization") String bearerToken
    );

    @GET("/laundry/hall/{id}")
    Observable<LaundryRoom> room(
            @Path("id") int id);

    @GET("/gsr/locations")
    Observable<List<GSRLocation>> location ();

    @GET("/gsr/availability/{id}/{gid}")
    Observable<GSR> gsrRoom(
            @Header("Authorization") String bearerToken,
            @Path("id") String id,
            @Path("gid") int gid,
            @Query("start") String date
            );

    @GET("/gsr/wharton")
    Observable<WhartonStatus> isWharton (
            @Header("Authorization") String bearerToken
    );

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

    @GET("/penndata/news")
    Observable<Article> getNews();

    @GET("/penndata/calendar")
    Observable<List<CalendarEvent>> getCalendar();

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

    @GET("/penndata/fitness/rooms/")
    Observable<List<FitnessRoom>> getFitnessRooms();

    @GET("/penndata/fitness/usage/{id}")
    Observable<FitnessRoomUsage> getFitnessRoomUsage(
            @Path("id") int id,
            @Query("num_samples") int samples,
            @Query("group_by") String groupBy);

    @GET("/penndata/fitness/preferences")
    Observable<List<Integer>> getFitnessPreferences(
            @Header("Authorization") String bearerToken);

    @POST("/penndata/fitness/preferences/")
    void sendFitnessPref(
            @Header("Authorization") String bearerToken,
            @Body FitnessRequest rooms,
            Callback<Response> callback);
}
