/**
 * @file NetworkModule.kt
 * @brief Hilt module for providing network-related singleton components.
 *
 * This module is responsible for setting up and providing all dependencies required for
 * network operations throughout the application. It includes the configuration for Gson,
 * OkHttpClient, Retrofit, and the specific API service interfaces. All dependencies
 * provided here are scoped as singletons to ensure a single, shared instance is used
 * across the app.
 */
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

    /**
     * Provides a customized [Gson] instance for Retrofit.
     * This instance is configured with several custom type adapters to handle the specific
     * JSON structures from the Penn Mobile API.
     */
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

    /**
     * Provides an [HttpLoggingInterceptor] for debugging network requests.
     * This interceptor is configured to log the body of network requests and responses,
     * which is invaluable for debugging during development.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    /**
     * Provides the application-wide [OkHttpClient].
     * This client is configured with connection timeouts and the logging interceptor.
     *
     * @param logging The [HttpLoggingInterceptor] to be added for network debugging.
     */
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

    /**
     * Provides the application-wide [Retrofit] instance.
     * This instance is configured with the base URL, the custom OkHttpClient, and
     * multiple converter factories to handle different response types.
     *
     * @param gson The custom [Gson] instance for JSON serialization/deserialization.
     * @param client The configured [OkHttpClient] for making requests.
     */
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

    /**
     * Provides the [StudentLife] API service interface.
     * Retrofit creates an implementation of this interface to handle API calls.
     *
     * @param retrofit The configured [Retrofit] instance.
     */
    @Provides
    @Singleton
    fun providesStudentLife(retrofit: Retrofit): StudentLife = retrofit.create(StudentLife::class.java)
}