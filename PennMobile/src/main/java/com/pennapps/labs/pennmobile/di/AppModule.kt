package com.pennapps.labs.pennmobile.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * First ever Dagger Hilt module written for Penn Mobile.
 *
 * It is responsible for providing foundational objects
 * like SharedPreferences and a main-thread CoroutineScope.
 *
 * Created by Andrew Chelimo on 2/11/2025
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Provides a coroutine scope that is tied to the application lifecycle
     */
    @Singleton
    @Provides
    @AppScope
    fun providesAppCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
