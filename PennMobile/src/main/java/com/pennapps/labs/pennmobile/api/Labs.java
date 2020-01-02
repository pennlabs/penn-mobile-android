package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.FlingEvent;
import com.pennapps.labs.pennmobile.classes.GSR;
import com.pennapps.labs.pennmobile.classes.GSRBookingResult;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.HomeCell;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.LaundryUsage;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.Review;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Adel on 12/13/14.
 * Retrofit interface to the Penn Labs API
 */
public interface Labs {
    @GET("/registrar/search")
    Observable<List<Course>> courses(
            @Query("q") String name);

    @GET("/directory/search")
    Observable<List<Person>> people(
            @Query("name") String name);

    @GET("/buildings/search")
    Observable<List<Building>> buildings(
            @Query("q") String name);

    @GET("/dining/venues")
    Observable<List<Venue>> venues();

    @GET("/dining/daily_menu/{id}")
    Observable<DiningHall> daily_menu(
            @Path("id") int id);

    @GET("/transit/stops")
    Observable<List<BusStop>> bus_stops();

    @GET("/transit/routes")
    Observable<List<BusRoute>> routes();

    @GET("/transit/routing")
    Observable<BusRoute> routing(
            @Query("latFrom") String latFrom,
            @Query("latTo") String latTo,
            @Query("lonFrom") String lonFrom,
            @Query("lonTo") String lonTo);

    @GET("/pcr/{id}")
    Observable<Review> course_review(
            @Path("id") String id);

    // new
    @GET("/laundry/halls/ids")
    Observable<List<LaundryRoomSimple>> laundryRooms();

    // new
    @GET("/laundry/hall/{id}")
    Observable<LaundryRoom> room(
            @Path("id") int id);

    @GET("/studyspaces/locations")
    Observable<List<GSRLocation>> location ();

    @GET("/studyspaces/availability/{id}")
    Observable<GSR> gsrRoom(
            @Path("id") int id,
            @Query("date") String date
            );

    @FormUrlEncoded
    @POST("/studyspaces/book")
    void bookGSR(
            @Field("sessionid") String sessionID,
            @Field("lid") int building,
            @Field("room") int room,
            @Field("start") String start,
            @Field("end") String end,
            @Field("firstname") String firstName,
            @Field("lastname") String lastName,
            @Field("email") String email,
            @Field("groupname") String groupname,
            @Field("phone") String phone,
            @Field("size") String size,
            Callback<GSRBookingResult> callback);

    @GET("/laundry/usage/{id}")
    Observable<LaundryUsage> usage(
            @Path("id") int id);

    @GET("/events/fling")
    Observable<List<FlingEvent>> getFlingEvents();

    // used for testing purposes only
    @GET("/laundry/preferences")
    Observable<List<Integer>> testPref(
            @Header("X-Device-ID") String deviceID);

    @FormUrlEncoded
    @POST("/laundry/preferences")
    void sendLaundryPref(
            @Header("X-Device-ID") String deviceID,
            @Field("rooms") String rooms,
            Callback<Response> callback);

    // home page
    @GET("/homepage")
    Observable<List<HomeCell>> getHomePage(
            @Header("X-Device-ID") String deviceID,
            @Header("X-Account-ID") String accountID,
            @Query("sessionid") String sessionID);

    @GET("/fitness/schedule")
    Observable<List<Gym>> getGymData();

    @GET("/studyspaces/reservations")
    Observable<List<GSRReservation>> getGsrReservations(
            @Query("email") String email,
            @Query("sessionid") String sessionID);

    @FormUrlEncoded
    @POST("/studyspaces/cancel")
    void cancelReservation(
            @Header("X-Device-ID") String deviceID,
            @Field("booking_id") String bookingID,
            @Field("sessionid") String sessionID,
            Callback<Response> callback);
}
