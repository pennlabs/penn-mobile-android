package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Building;
import com.pennapps.labs.pennmobile.classes.Course;
import com.pennapps.labs.pennmobile.classes.DiningHall;
import com.pennapps.labs.pennmobile.classes.NewDiningHall;
import com.pennapps.labs.pennmobile.classes.Person;
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
    List<Course> courses(
        @Query("q") String name);

    @GET("/directory/search")
    List<Person> people(
        @Query("name") String name);

    @GET("/buildings/search")
    Observable<List<Building>> buildings(
        @Query("q") String name);

    @GET("/dining/venues")
    List<Venue> venues();

    @GET("/dining/daily_menu/{id}")
    NewDiningHall daily_menu(
        @Path("id") int id);
}
