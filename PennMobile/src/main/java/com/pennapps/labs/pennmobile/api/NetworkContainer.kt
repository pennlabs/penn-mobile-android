package com.pennapps.labs.pennmobile.api

import StudentLife2
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.classes.Contact
import com.pennapps.labs.pennmobile.classes.Venue
import com.pennapps.labs.pennmobile.api.Serializer.*
import com.pennapps.labs.pennmobile.classes.Account
import com.pennapps.labs.pennmobile.classes.DiningHall
import com.pennapps.labs.pennmobile.classes.FlingEvent
import com.pennapps.labs.pennmobile.classes.GSRLocation
import com.pennapps.labs.pennmobile.classes.GSRReservation
import com.pennapps.labs.pennmobile.classes.LaundryRoom
import com.pennapps.labs.pennmobile.classes.LaundryRoomSimple
import com.pennapps.labs.pennmobile.classes.LaundryUsage
import com.pennapps.labs.pennmobile.classes.Post
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkContainer {
    val pennMobileAPI: StudentLife2
    private val endpoint : String = "https://pennmobile.org/api/"
    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Contact?>?>() {}.type,
            DataSerializer<Any?>()
        )
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Venue?>?>() {}.type,
            VenueSerializer()
        )
        gsonBuilder.registerTypeAdapter(DiningHall::class.java, MenuSerializer())
        // gets room
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<LaundryRoom?>() {}.type,
            LaundryRoomSerializer()
        )
        // gets laundry room list
        gsonBuilder.registerTypeAdapter(object :
            TypeToken<MutableList<LaundryRoomSimple?>?>() {}.type, LaundryRoomListSerializer())
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<GSRLocation?>?>() {}.type,
            GsrLocationSerializer()
        )
        // gets laundry usage
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<LaundryUsage?>() {}.type,
            LaundryUsageSerializer()
        )
        // gets laundry preferences (used only for testing)
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Int?>?>() {}.type,
            LaundryPrefSerializer()
        )
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<FlingEvent?>?>() {}.type,
            FlingEventSerializer()
        )
        // gets gsr reservations
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<GSRReservation?>?>() {}.type,
            GsrReservationSerializer()
        )
        // gets user
        gsonBuilder.registerTypeAdapter(Account::class.java, UserSerializer())
        // gets posts
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Post?>?>() {}.type,
            PostsSerializer()
        )
        val gson = GsonBuilder().create()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(35, TimeUnit.SECONDS)
            .readTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(35, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(endpoint)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        pennMobileAPI = retrofit.create(StudentLife2::class.java)
    }

}
