package com.mitsuki.ehit.crutch.di

import com.mitsuki.ehit.model.repository.CommentRepository
import com.mitsuki.ehit.model.repository.DownloadRepository
import com.mitsuki.ehit.model.repository.PagingRepository
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.model.repository.impl.CommentRepositoryImpl
import com.mitsuki.ehit.model.repository.impl.DownloadRepositoryImpl
import com.mitsuki.ehit.model.repository.impl.PagingRepositoryImpl
import com.mitsuki.ehit.model.repository.impl.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBinds {
    @RemoteRepository
    @Singleton
    @Binds
    abstract fun getRepository(impl: RepositoryImpl): Repository

    @Singleton
    @Binds
    abstract fun pagingSource(impl: PagingRepositoryImpl): PagingRepository

    @Singleton
    @Binds
    abstract fun comment(impl: CommentRepositoryImpl): CommentRepository

    @Singleton
    @Binds
    abstract fun downloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
}