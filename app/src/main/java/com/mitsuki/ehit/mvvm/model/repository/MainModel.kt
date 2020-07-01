package com.mitsuki.ehit.mvvm.model.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mitsuki.ehit.being.Url
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetail
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.mvvm.model.okhttp.HttpRookie
import com.mitsuki.ehit.mvvm.model.okhttp.fakeHeader
import com.mitsuki.mvvm.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class MainModel @Inject constructor() : BaseModel() {

    suspend fun galleryList(config: PagingConfig) = withContext(Dispatchers.IO) {
        val list = ArrayList<Gallery>()
        val response =
            HttpRookie.client
                .newCall(Request.Builder().url(Url.list()).fakeHeader().build())
                .execute()
        list.addAll(Gallery.parseList(response.body?.string()!!))
        Pager(config) {
            GalleryPagingSource(list)
        }.flow
    }

    suspend fun galleryDetail(gid: Long, token: String) = withContext(Dispatchers.IO) {
        val response =
            HttpRookie.client
                .newCall(Request.Builder().url(Url.galleryDetail(gid, token)).fakeHeader().build())
                .execute()
        val galleryDetail = GalleryDetail.parse(response.body?.string()!!)

        Pager(PagingConfig(pageSize = 25)) {
            GalleryDetailPagingSource(GalleryDetailWrap.parse(galleryDetail))
        }.flow
    }
}