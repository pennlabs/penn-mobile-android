package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.BusRoute;
import com.pennapps.labs.pennmobile.classes.BusStop;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.LaundryRoom;
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple;
import com.pennapps.labs.pennmobile.classes.Person;
import com.pennapps.labs.pennmobile.classes.Review;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import retrofit.http.GET;
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


}
