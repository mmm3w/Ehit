package com.mitsuki.ehit.being.okhttp

import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

class RequestProvider {
    //画廊列表
    fun galleryListRequest(page: Int = 0): Request {
        return Request.Builder()
            .get()
            .url(Url.galleryList(page))
            .fakeHeader()
            .build()
    }

    //画廊详情
    fun galleryDetailRequest(gid: Long, token: String): Request {
        return Request.Builder()
            .get()
            .url(Url.galleryDetail(gid, token))
            .fakeHeader()
            .build()
    }
}
