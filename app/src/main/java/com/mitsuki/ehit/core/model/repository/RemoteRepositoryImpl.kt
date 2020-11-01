package com.mitsuki.ehit.core.model.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.ehit.being.MemoryCache
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.being.okhttp.RequestResult
import com.mitsuki.ehit.being.okhttp.execute
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.GalleryPreview
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryListSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val requestProvider: RequestProvider
) : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    override fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig, initialKey = 0) {
            GalleryListSource(pageIn, requestProvider)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: PageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = 0) {
            GalleryDetailSource(gid, token, pageIn, detailSource, requestProvider)
        }.flow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryPreview(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.Default) {

            val data = MemoryCache.getImagePage(gid, index)
            if (data != null) {
                RequestResult.SuccessResult(data)
            } else {
                val res = requestProvider.galleryPreviewRequest(gid, token, index).execute()
                val bodyStr = res?.body?.string()
                if (bodyStr == null || res.code != 200) {
                    RequestResult.FailResult(Throwable(""))
                } else {
                    try {
                        RequestResult.SuccessResult(
                            GalleryPreview.parse(bodyStr)
                                .apply { MemoryCache.cacheImagePage(gid, index, this) })
                    } catch (e: Exception) {
                        RequestResult.FailResult<GalleryPreview>(e)
                    }
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryDetailWithPToken(gid: Long, token: String, index: Int): RequestResult<String> {
        return withContext(Dispatchers.Default) {
            val res = requestProvider.galleryDetailRequest(gid, token, index).execute()
            val bodyStr = res?.body?.string()
            if (bodyStr == null || res.code != 200){
                RequestResult.FailResult(Throwable(""))
            }else{
                try {
                    val images = ImageSource.parse(bodyStr)
                    MemoryCache.cacheImageToken(gid, images.data)
                    val pToken = MemoryCache.getImageToken(gid, index) ?: ""
                    if (pToken.isEmpty()) throw Exception("not found pToken")
                    RequestResult.SuccessResult(pToken)
                }catch (e:Exception){
                    RequestResult.FailResult<String>(e)
                }
            }
        }
    }

}