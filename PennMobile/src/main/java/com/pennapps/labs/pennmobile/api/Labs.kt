package com.pennapps.labs.pennmobile.api

import com.google.gson.annotations.JsonAdapter
import com.pennapps.labs.pennmobile.classes.Building
import com.pennapps.labs.pennmobile.classes.BusRoute
import com.pennapps.labs.pennmobile.classes.BusStop
import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.pennapps.labs.pennmobile.classes.GSR
import com.pennapps.labs.pennmobile.classes.GSRBookingResult
import com.pennapps.labs.pennmobile.classes.GSRLocation
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.pennapps.labs.pennmobile.classes.Gym
import com.pennapps.labs.pennmobile.classes.HomeCell
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.classes.OAuthUser
import com.pennapps.labs.pennmobile.classes.Person
import com.pennapps.labs.pennmobile.classes.Review
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.SaveAccountResponse
import com.pennapps.labs.pennmobile.classes.Venue

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Body
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.Headers
import retrofit.http.POST
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable

/**
 * Created by Adel on 12/13/14.
 * Retrofit interface to the Penn Labs API
 */
interface Labs {

    @get:GET("/events/fling")
    val flingEvents: Observable<List<FlingEvent>>

    @get:GET("/fitness/schedule")
    val gymData: Observable<List<Gym>>

    @GET("/registrar/search/person/{person_id}")
    fun user(
            @Path("person_id") person_id: String): Observable<Account>

    @GET("/registrar/search")
    fun courses(
            @Query("q") name: String): Observable<List<Course>>

    @GET("/directory/search")
    fun people(
            @Query("name") name: String): Observable<List<Person>>

    @GET("/buildings/search")
    fun buildings(
            @Query("q") name: String): Observable<List<Building>>

    @GET("/dining/venues")
    fun venues(): Observable<List<Venue>>

    @GET("/dining/daily_menu/{id}")
    fun daily_menu(
            @Path("id") id: Int): Observable<DiningHall>

    @GET("/transit/stops")
    fun bus_stops(): Observable<List<BusStop>>

    @GET("/transit/routes")
    fun routes(): Observable<List<BusRoute>>

    @GET("/transit/routing")
    fun routing(
            @Query("latFrom") latFrom: String,
            @Query("latTo") latTo: String,
            @Query("lonFrom") lonFrom: String,
            @Query("lonTo") lonTo: String): Observable<BusRoute>

    @GET("/pcr/{id}")
    fun course_review(
            @Path("id") id: String): Observable<Review>

    @GET("/laundry/halls/ids")
    fun laundryRooms(): Observable<List<LaundryRoomSimple>>

    @GET("/laundry/hall/{id}")
    fun room(
            @Path("id") id: Int): Observable<LaundryRoom>

    @GET("/studyspaces/locations")
    fun location(): Observable<List<GSRLocation>>

    @GET("/studyspaces/availability/{id}")
    fun gsrRoom(
            @Path("id") id: Int,
            @Query("date") date: String
    ): Observable<GSR>

    @FormUrlEncoded
    @POST("/studyspaces/book")
    fun bookGSR(
            @Field("sessionid") sessionID: String?,
            @Field("lid") building: Int,
            @Field("room") room: Int,
            @Field("start") start: String,
            @Field("end") end: String,
            @Field("firstname") firstName: String,
            @Field("lastname") lastName: String,
            @Field("email") email: String,
            @Field("groupname") groupname: String,
            @Field("phone") phone: String,
            @Field("size") size: String,
            callback: Callback<GSRBookingResult>)

    @GET("/laundry/usage/{id}")
    fun usage(
            @Path("id") id: Int): Observable<LaundryUsage>

    // home page
    @GET("/homepage")
    fun getHomePage(
            @Header("X-Device-ID") deviceID: String?,
            @Header("X-Account-ID") accountID: String?,
            @Query("sessionid") sessionID: String?): Observable<List<HomeCell>>

    @GET("/studyspaces/reservations")
    fun getGsrReservations(
            @Query("email") email: String?,
            @Query("sessionid") sessionID: String?): Observable<List<GSRReservation>>

    @FormUrlEncoded
    @POST("/studyspaces/cancel")
    fun cancelReservation(
            @Header("X-Device-ID") deviceID: String?,
            @Field("booking_id") bookingID: String,
            @Field("sessionid") sessionID: String?,
            callback: Callback<Response>)

    // accounts
    @Headers("Content-Type: application/json")
    @POST("/account/register")
    fun saveAccount(
            @Body account: Account,
            callback: Callback<SaveAccountResponse>)

    @GET("/laundry/preferences")
    fun getLaundryPref(
            @Header("X-Device-ID") deviceID: String): Observable<List<Int>>

    @FormUrlEncoded
    @POST("/laundry/preferences")
    fun sendLaundryPref(
            @Header("X-Device-ID") deviceID: String,
            @Field("rooms") rooms: String,
            callback: Callback<Response>)

    @FormUrlEncoded
    @POST("/dining/preferences")
    fun sendDiningPref(
            @Header("X-Device-ID") deviceID: String,
            @Field("venues") venues: String,
            callback: Callback<Response>)
}
