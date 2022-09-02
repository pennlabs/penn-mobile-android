package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Account;
import com.pennapps.labs.pennmobile.classes.Article;
import com.pennapps.labs.pennmobile.classes.CalendarEvent;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.DiningPreferences;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSR;
import com.pennapps.labs.pennmobile.classes.GSRBookingResult;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.HomeCell;
import com.pennapps.labs.pennmobile.classes.HomeCellInfo;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.SaveAccountResponse;
import com.pennapps.labs.pennmobile.classes.Venue;

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

    @GET("/dining/venues")
    Observable<List<Venue>> venues();

    @GET("/dining/weekly_menu/{id}")
    Observable<DiningHall> daily_menu(
            @Path("id") int id);

    @GET("/dining/preferences")
    Observable<DiningPreferences> getDiningPreferences(
            @Header("Authorization") String bearerToken
    );

    @GET("/laundry/halls/ids")
    Observable<List<LaundryRoomSimple>> laundryRooms();

    @GET("/laundry/hall/{id}")
    Observable<LaundryRoom> room(
            @Path("id") int id);

    @GET("/gsr/locations")
    Observable<List<GSRLocation>> location ();

    @GET("/gsr/availability/{id}/{gid}")
    Observable<GSR> gsrRoom(
            @Path("id") int id,
            @Path("gid") int gid,
            @Query("start") String date
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

    @GET("/laundry/usage/{id}")
    Observable<LaundryUsage> usage(
            @Path("id") int id);

    @GET("/events/fling")
    Observable<List<FlingEvent>> getFlingEvents();

    @GET("/penndata/news")
    Observable<Article> getNews();

    @GET("/penndata/calendar")
    Observable<List<CalendarEvent>> getCalendar();

    // home page
    @GET("/penndata/homepage")
    Observable<List<HomeCell>> getHomePage(
            @Header("Authorization") String bearerToken
    );


    @GET("/fitness/schedule")
    Observable<List<Gym>> getGymData();

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

    @FormUrlEncoded
    @POST("/laundry/preferences")
    void sendLaundryPref(
            @Header("Authorization") String bearerToken,
            @Field("rooms") String rooms,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/dining/preferences")
    void sendDiningPref(
            @Header("Authorization") String bearerToken,
            @Field("venues") String venues,
            Callback<Response> callback);
}