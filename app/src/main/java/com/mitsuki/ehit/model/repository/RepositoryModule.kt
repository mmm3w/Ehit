package com.mitsuki.ehit.model.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
annotation class RemoteRepository

@Module
@InstallIn(ApplicationComponent::class)
abstract class RemoteRepositoryModule {
    @RemoteRepository
    @Singleton
    @Binds
    abstract fun getRepository(impl: RemoteRepositoryImpl): Repository
}