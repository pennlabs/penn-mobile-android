package com.pennapps.labs.pennmobile.di

import android.content.Context
import androidx.room.Room
import com.pennapps.labs.pennmobile.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "appDatabase",
        ).fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesFavouriteDiningHallsDao(appDatabase: AppDatabase) =
        appDatabase.favouriteDiningHallsDao
}


