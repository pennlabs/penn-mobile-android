package com.pennapps.labs.pennmobile.api

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
import com.squareup.okhttp.OkHttpClient
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.GsonConverter
import java.util.concurrent.TimeUnit

class NetworkContainer {
    val pennMobileAPI: StudentLife
    private val endpoint : String = "https://pennmobile.org/api"
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
        val gson = gsonBuilder.create()
        val okHttpClient = OkHttpClient()
        okHttpClient.setConnectTimeout(35, TimeUnit.SECONDS) // Connection timeout
        okHttpClient.setReadTimeout(35, TimeUnit.SECONDS)    // Read timeout
        okHttpClient.setWriteTimeout(35, TimeUnit.SECONDS)   // Write timeout
        val restAdapter = RestAdapter.Builder()
            .setConverter(GsonConverter(gson))
            .setClient(OkClient(okHttpClient))
            .setEndpoint(endpoint)
            .build()
        pennMobileAPI = restAdapter.create(StudentLife::class.java)
    }

}
