package com.mitsuki.ehit.crutch.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiClientCreator

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoilClientCreator

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AsCookieManager





