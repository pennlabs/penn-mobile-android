package com.pennapps.labs.pennmobile.di

import com.pennapps.labs.pennmobile.dining.repo.DiningRepo
import com.pennapps.labs.pennmobile.dining.repo.DiningRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindingDiningRepo(diningRepoImpl: DiningRepoImpl): DiningRepo

}