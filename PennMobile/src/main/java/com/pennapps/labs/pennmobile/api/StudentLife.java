package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.dining.classes.DiningHall;
import com.pennapps.labs.pennmobile.fling.classes.FlingEvent;
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation;
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation;
import com.pennapps.labs.pennmobile.home.classes.Post;
import com.pennapps.labs.pennmobile.dining.classes.Venue;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
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

    @GET("/events/fling")
    Observable<List<FlingEvent>> getFlingEvents();

    @GET("/gsr/reservations")
    Observable<List<GSRReservation>> getGsrReservations(
            @Header("Authorization") String bearerToken
    );

    @GET("/laundry/preferences")
    Observable<List<Integer>> getLaundryPref(
            @Header("Authorization") String bearerToken);

    @GET("/portal/posts/browse/")
    Observable<List<Post>> validPostsList(
            @Header("Authorization") String bearerToken
    );
}
