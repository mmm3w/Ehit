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
            .build()
    }

    //画廊详情
    fun galleryDetailRequest(gid: Long, token: String, index: Int = 0): Request {
        return Request.Builder()
            .get()
            .url(Url.galleryDetail(gid, token, index))
            .build()
    }

    //单图预览
    fun galleryPreviewRequest(gid: Long, token: String, index: Int = 0, nl: String? = null)
            : Request {
        return Request.Builder()
            .get()
            .url(Url.galleryPreviewDetail(gid, token, index, nl))
            .build()
    }
}
