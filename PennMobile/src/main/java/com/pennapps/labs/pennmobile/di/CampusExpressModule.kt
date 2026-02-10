package com.pennapps.labs.pennmobile.di

import com.pennapps.labs.pennmobile.api.CampusExpress
import com.pennapps.labs.pennmobile.api.Platform
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CampusExpressModule {
    @Provides
    @Singleton
    @Named("CampusExpressRetrofit") // Tag this specific instance
    fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(Platform.campusExpressBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Tell Hilt to use the tagged instance
    @Provides
    @Singleton
    fun provideCampusExpress(
        @Named("CampusExpressRetrofit") retrofit: Retrofit,
    ): CampusExpress = retrofit.create(CampusExpress::class.java)
}
