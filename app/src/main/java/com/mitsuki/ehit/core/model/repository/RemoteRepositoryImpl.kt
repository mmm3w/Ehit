package com.mitsuki.ehit.core.model.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.being.MemoryCache
import com.mitsuki.ehit.being.network.RequestResult
import com.mitsuki.ehit.being.network.Url
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.convert.GalleryPreviewConvert
import com.mitsuki.ehit.core.model.convert.ImageSourceConvert
import com.mitsuki.ehit.core.model.convert.LoginConvert
import com.mitsuki.ehit.core.model.entity.*
import com.mitsuki.ehit.core.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.core.model.pagingsource.GalleryListSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.log

class RemoteRepositoryImpl @Inject constructor() : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    override fun galleryList(pageIn: PageIn): Flow<PagingData<Gallery>> {
        Log.e("RemoteRepositoryImpl","galleryList")
        return Pager(mListPagingConfig, initialKey = 0) {
            GalleryListSource(pageIn)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: PageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = 0) {
            GalleryDetailSource(gid, token, pageIn, detailSource)
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
                val remoteData: Response<GalleryPreview> = HttpRookie
                    .get<GalleryPreview>(Url.galleryPreviewDetail(gid, token, index)) {
                        convert = GalleryPreviewConvert()
                    }
                    .execute()

                try {
                    when (remoteData) {
                        is Response.Success<GalleryPreview> -> RequestResult.SuccessResult(
                            remoteData.requireBody()
                                .apply { MemoryCache.cacheImagePage(gid, index, this) }
                        )
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                } catch (inner: Throwable) {
                    RequestResult.FailResult<GalleryPreview>(inner)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryDetailWithPToken(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<String> {
        return withContext(Dispatchers.Default) {

            val remoteData = HttpRookie
                .get<PageInfo<ImageSource>>(Url.galleryDetail(gid, token)) {
                    convert = ImageSourceConvert()
                    if (index != 0) urlParams(Url.PAGE_DETAIL to index.toString())
                }
                .execute()

            try {
                when (remoteData) {
                    is Response.Success<PageInfo<ImageSource>> -> {
                        val pToken = remoteData.requireBody().run {
                            MemoryCache.cacheImageToken(gid, data)
                            MemoryCache.getImageToken(gid, index)
                        } ?: throw Exception("not found pToken")

                        RequestResult.SuccessResult(pToken)
                    }
                    is Response.Fail<*> -> throw remoteData.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult<String>(inner)
            }
        }
    }

    override suspend fun login(account: String, password: String): RequestResult<String> {
        return withContext(Dispatchers.Default) {
            val loginData = HttpRookie
                .post<String>(Url.login()) {
                    convert = LoginConvert()
                    params(Url.REFERER to "https://forums.e-hentai.org/index.php?")
                    params(Url.B to "")
                    params(Url.BT to "")

                    params(Url.USER_NAME to account)
                    params(Url.PASS_WORD to password)
                    params(Url.COOKIE_DATE to "1")
                    //params(Url.PRIVACY to "1")

                    header("Origin" to "https://forums.e-hentai.org")
                    header("Referer" to "https://forums.e-hentai.org/index.php?act=Login&CODE=00")
                }
                .execute()
            try {
                when (loginData) {
                    is Response.Success<String> -> RequestResult.SuccessResult(loginData.requireBody())
                    is Response.Fail<*> -> throw loginData.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult<String>(inner)

            }
        }
    }


}