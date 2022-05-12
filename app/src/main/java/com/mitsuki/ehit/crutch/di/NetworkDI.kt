package com.mitsuki.ehit.crutch.di

import com.mitsuki.ehit.crutch.network.client.ClientCreator
import com.mitsuki.ehit.crutch.network.CookieManager
import com.mitsuki.ehit.crutch.network.client.MyApiClientCreator
import com.mitsuki.ehit.crutch.network.client.MyCoilClientCreator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBinds {
    //cookie处理
    @Singleton
    @Binds
    abstract fun cookieJar(impl: CookieManager): CookieJar

    //请求用
    @ApiClientCreator
    @Binds
    abstract fun apiClientCreator(creator: MyApiClientCreator): ClientCreator

    //图片加载用
    @CoilClientCreator
    @Binds
    abstract fun coilClientCreator(creator: MyCoilClientCreator): ClientCreator
}


@Module
@InstallIn(SingletonComponent::class)
object NetworkProviders {

    @AsCookieManager
    @Provides
    fun cookieManager(cookieJar: CookieJar): CookieManager {
        return cookieJar as CookieManager
    }

    @Singleton
    @Provides
    fun okhttpClient(@ApiClientCreator creator: ClientCreator): OkHttpClient {
        return creator.create()
    }
}

