package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.classes.Course
import com.pennapps.labs.pennmobile.classes.PCARegistrationBody
import com.pennapps.labs.pennmobile.classes.PennCourseAlertRegistration
import com.pennapps.labs.pennmobile.classes.PennCourseAlertUpdateBody
import com.pennapps.labs.pennmobile.classes.Profile
import com.pennapps.labs.pennmobile.classes.Section
import com.pennapps.labs.pennmobile.classes.UserInfo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://penncoursealert.com"

var okHttpClient: OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

private val retrofit =
    Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

interface PennCourseAlertApiService {
    @GET("/api/alert/registrations/")
    fun getAllRegistrations(
        @Header("Authorization") token: String,
    ): Call<List<PennCourseAlertRegistration>>

    @POST("/api/alert/registrations/")
    fun createRegistration(
        @Body registration: PCARegistrationBody,
        @Header("Authorization") token: String,
    ): Call<String>

    @GET("/api/alert/registrations/{id}/")
    fun getRegistrationById(
        @Path("id") id: String,
        @Header("Authorization") token: String,
    ): Call<PennCourseAlertRegistration>

    @PUT("/api/alert/registrations/{id}/")
    fun updateRegistrationById(
        @Path("id") id: String,
        @Body updateRegistrationBody: PennCourseAlertUpdateBody,
        @Header("Authorization") token: String,
    ): Call<String>

    @GET("/api/base/{semester}/courses/")
    fun getCourses(
        @Path("semester") semester: String,
    ): Call<List<Course>>

    @GET("/api/base/{semester}/search/courses/")
    fun getCourses(
        @Path("semester") semester: String,
        @Query("search") search: String,
        @Query("type") type: String,
    ): Call<List<Course>>

    @GET("/api/base/{semester}/search/sections/")
    fun getSections(
        @Path("semester") semester: String,
        @Query("search") search: String,
    ): Call<List<Section>>

    @PATCH("/accounts/me")
    fun updateInfo(
        @Body profile: Profile,
        @Header("Authorization") token: String,
    ): Call<String>

    @GET("/accounts/me")
    fun retrieveUser(
        @Header("Authorization") token: String,
    ): Call<UserInfo>
}

object PennCourseAlertApi {
    val retrofitService: PennCourseAlertApiService by lazy {
        retrofit.create(PennCourseAlertApiService::class.java)
    }
}
