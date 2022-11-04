package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.classes.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://penncoursealert.com"

//TODO: (ALI) change into fetching token from VM
private const val testToken = "U1OuY6Nk3IhiBCQ30dTXkciMk0Mtxo"

var okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(1, TimeUnit.MINUTES)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface PennCourseAlertApiService {
    @Headers("Authorization: Bearer $testToken")
    @GET("/api/alert/registrations/")
    fun getAllRegistrations():
            Call<List<PennCourseAlertRegistration>>

    @Headers("Authorization: Bearer $testToken")
    @POST("/api/alert/registrations/")
    fun createRegistration(@Body registration: PCARegistrationBody):
            Call<String>

    @Headers("Authorization: Bearer $testToken")
    @GET("/api/alert/registrations/{id}/")
    fun getRegistrationById(@Path("id") id: String):
            Call<PennCourseAlertRegistration>

    @Headers("Authorization: Bearer $testToken")
    @PUT("/api/alert/registrations/{id}/")
    fun updateRegistrationById(@Path("id") id: String, @Body updateRegistrationBody: PennCourseAlertUpdateBody):
            Call<String>

    @GET("/api/base/{semester}/courses/")
    fun getCourses(@Path("semester") semester: String):
            Call<List<Course>>

    @GET("/api/base/{semester}/search/courses/")
    fun getCourses(@Path("semester") semester: String, @Query("search") search: String, @Query("type") type: String):
            Call<List<Course>>

    @GET("/api/base/{semester}/search/sections/")
    fun getSections(@Path("semester") semester: String, @Query("search") search: String):
            Call<List<Section>>


}

object PennCourseAlertApi {
    val retrofitService : PennCourseAlertApiService by lazy {
        retrofit.create(PennCourseAlertApiService::class.java)
    }
}