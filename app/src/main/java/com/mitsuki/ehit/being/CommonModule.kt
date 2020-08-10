package com.mitsuki.ehit.being

import androidx.paging.PagingConfig
import com.mitsuki.ehit.being.okhttp.RequestProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CommonModule {

    //统一分页配置
    @Provides
    @Singleton
    fun pagingConfig(): PagingConfig {
        return PagingConfig(pageSize = 25, initialLoadSize = 50, prefetchDistance = 1)
    }

    //request
    @Provides
    @Singleton
    fun requestProvider(): RequestProvider {
        return RequestProvider()
    }

}