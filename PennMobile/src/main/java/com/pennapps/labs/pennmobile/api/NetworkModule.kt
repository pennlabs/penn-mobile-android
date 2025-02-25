package com.pennapps.labs.pennmobile.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.api.classes.Account
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.Venue
import com.pennapps.labs.pennmobile.fling.classes.FlingEvent
import com.pennapps.labs.pennmobile.gsr.classes.GSRLocation
import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation
import com.pennapps.labs.pennmobile.home.classes.Post
import com.pennapps.labs.pennmobile.laundry.classes.LaundryRoom
import com.pennapps.labs.pennmobile.more.classes.Contact
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideCampusExpress(): CampusExpress {
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(Platform.campusExpressBaseUrl)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        return retrofit.create(CampusExpress::class.java)
    }

    @Provides
    fun providePlatform(): Platform {
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(Platform.platformBaseUrl)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

        return retrofit.create(Platform::class.java)
    }

    @Provides
    fun provideStudentLife(): StudentLife {

        val gsonBuilder = GsonBuilder()

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Contact?>?>() {}.type,
            Serializer.DataSerializer<Any?>(),
        )

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Venue?>?>() {}.type,
            Serializer.VenueSerializer(),
        )
        gsonBuilder.registerTypeAdapter(
            DiningHall::class.java,
            Serializer.MenuSerializer(),
        )
        // gets room
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<LaundryRoom?>() {}.type,
            Serializer.LaundryRoomSerializer(),
        )

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<GSRLocation?>?>() {}.type,
            Serializer.GsrLocationSerializer(),
        )

        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<FlingEvent?>?>() {}.type,
            Serializer.FlingEventSerializer(),
        )

        // gets gsr reservations
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<GSRReservation?>?>() {}.type,
            Serializer.GsrReservationSerializer(),
        )
        // gets user
        gsonBuilder.registerTypeAdapter(
            Account::class.java,
            Serializer.UserSerializer(),
        )
        // gets posts
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<MutableList<Post?>?>() {}.type,
            Serializer.PostsSerializer(),
        )

        val gson = gsonBuilder.create()

        val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        val okHttpClient =
            OkHttpClient
                .Builder()
                .connectTimeout(35, TimeUnit.SECONDS)
                .readTimeout(35, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl("https://pennmobile.org/api/")
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(StudentLife::class.java)
    }

    @Provides
    fun provideNetworkManager(
        campusExpress: CampusExpress,
        platform: Platform,
        studentLife: StudentLife,
        @ApplicationContext context: Context
    ): NetworkManager {
        return NetworkManager(campusExpress, platform, studentLife, context)
    }
}

