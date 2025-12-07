package com.pennapps.labs.pennmobile.di

import android.content.Context
import com.pennapps.labs.pennmobile.api.CampusExpressNetworkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CampusExpressNetworkManagerModule {
    @Provides
    @Singleton
    fun provideCampusExpressNetworkManager(
        @ApplicationContext context: Context,
    ): CampusExpressNetworkManager = CampusExpressNetworkManager(context)
}
