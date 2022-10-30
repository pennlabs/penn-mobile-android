package com.pennapps.labs.pennmobile.api

import com.pennapps.labs.pennmobile.classes.PCARegistrationBody
import com.pennapps.labs.pennmobile.classes.PennCourseAlertRegistration
import com.pennapps.labs.pennmobile.classes.PennCourseAlertUpdateBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://penncoursealert.com"

//TODO: (ALI) change into fetching token from VM
private const val testToken = "Z8TU46VQWQFHZJ1wNAapG6fTLLkbgR"

//TODO: (ALI) change converter factory into sth for JSON (Moshi or GSON)
private val retrofit = Retrofit.Builder()
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
    fun createRegistration(@Body registrationBody: PCARegistrationBody):
            Call<String>

    @Headers("Authorization: Bearer $testToken")
    @GET("/api/alert/registrations/{id}/")
    fun getRegistrationById(@Path("id") id: String):
            Call<PennCourseAlertRegistration>

    @Headers("Authorization: Bearer $testToken")
    @PUT("/api/alert/registrations/{id}/")
    fun updateRegistrationById(@Path("id") id: String, @Body updateRegistrationBody: PennCourseAlertUpdateBody):
            Call<String>

    //TODO: (ALI) add methods for the 2 registration history API calls

//    @Headers("Authorization: Bearer $testToken")
//    @GET("registrationhistory")
//    fun getRegistrationHistory():
//            Call<String>

}

object PennCourseAlertApi {
    val retrofitService : PennCourseAlertApiService by lazy {
        retrofit.create(PennCourseAlertApiService::class.java)
    }
}