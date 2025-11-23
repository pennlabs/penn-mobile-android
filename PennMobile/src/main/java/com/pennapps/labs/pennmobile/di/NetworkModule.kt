package com.pennapps.labs.pennmobile.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pennapps.labs.pennmobile.api.Serializer
import com.pennapps.labs.pennmobile.api.StudentLife
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
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PENN_MOBILE_BASE_URL = "https://pennmobile.org/api/"

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .apply {
                registerTypeAdapter(
                    object : TypeToken<MutableList<Contact?>?>() {}.type,
                    Serializer.DataSerializer<Any?>(),
                )

                registerTypeAdapter(
                    object : TypeToken<MutableList<Venue?>?>() {}.type,
                    Serializer.VenueSerializer(),
                )

                registerTypeAdapter(
                    DiningHall::class.java,
                    Serializer.MenuSerializer(),
                )
                // gets room
                registerTypeAdapter(
                    object : TypeToken<LaundryRoom?>() {}.type,
                    Serializer.LaundryRoomSerializer(),
                )

                registerTypeAdapter(
                    object : TypeToken<MutableList<GSRLocation?>?>() {}.type,
                    Serializer.GsrLocationSerializer(),
                )

                registerTypeAdapter(
                    object : TypeToken<MutableList<FlingEvent?>?>() {}.type,
                    Serializer.FlingEventSerializer(),
                )

                // gets gsr reservations
                registerTypeAdapter(
                    object : TypeToken<MutableList<GSRReservation?>?>() {}.type,
                    Serializer.GsrReservationSerializer(),
                )
                // gets user
                registerTypeAdapter(
                    Account::class.java,
                    Serializer.UserSerializer(),
                )
                // gets posts
                registerTypeAdapter(
                    object : TypeToken<MutableList<Post?>?>() {}.type,
                    Serializer.PostsSerializer(),
                )
            }.create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(35, TimeUnit.SECONDS)
            .readTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(35, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        gson: Gson,
        client: OkHttpClient,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(PENN_MOBILE_BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesStudentLife(retrofit: Retrofit): StudentLife = retrofit.create(StudentLife::class.java)
}
