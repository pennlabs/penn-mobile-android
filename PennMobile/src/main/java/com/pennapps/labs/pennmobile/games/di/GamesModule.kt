package com.pennapps.labs.pennmobile.games.di

import com.pennapps.labs.pennmobile.games.repo.GamesRepository
import com.pennapps.labs.pennmobile.games.repo.GamesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GamesModule {
    @Binds
    @Singleton
    abstract fun bindGamesRepository(gamesRepositoryImpl: GamesRepositoryImpl): GamesRepository
}
