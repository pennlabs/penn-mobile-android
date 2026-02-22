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
 * @brief Hilt module for providing application-wide singleton dependencies.
 *
 * First ever Dagger Hilt module written for Penn Mobile.
 *
 * This module is responsible for providing foundational objects that are used across
 * the entire application lifecycle, such as SharedPreferences and a global CoroutineScope.
 * All dependencies provided here are scoped as singletons.
 *
 * Created by Andrew Chelimo on 2/11/2025
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides a singleton instance of [SharedPreferences].
     *
     * @param context The application context, used to get the default SharedPreferences.
     */
    @Singleton
    @Provides
    fun providesSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Provides a coroutine scope that is tied to the application lifecycle.
     * This scope is configured with a SupervisorJob, ensuring that the failure of one
     * child coroutine does not cancel the entire scope. It uses the Main dispatcher.
     */
    @Singleton
    @Provides
    @AppScope
    fun providesAppCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}

/**
 * Qualifier to distinguish the application-level CoroutineScope from other scopes.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
