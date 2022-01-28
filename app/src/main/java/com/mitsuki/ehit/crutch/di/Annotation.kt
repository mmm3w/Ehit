package com.mitsuki.ehit.crutch.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoilClient



