package com.mitsuki.ehit.model.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.json
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.crutch.MemoryCache
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.crutch.toJson
import com.mitsuki.ehit.const.ParamValue
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.convert.GalleryPreviewConvert
import com.mitsuki.ehit.model.convert.ImageSourceConvert
import com.mitsuki.ehit.model.convert.LoginConvert
import com.mitsuki.ehit.model.convert.RateBackConvert
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.db.GalleryPreviewCache
import com.mitsuki.ehit.model.page.GalleryDetailPageIn
import com.mitsuki.ehit.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.model.pagingsource.GalleryListSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.ceil

class RepositoryImpl @Inject constructor() : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    override fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>> {
        Log.e("RemoteRepositoryImpl", "galleryList")
        return Pager(mListPagingConfig, initialKey = 0) {
            GalleryListSource(pageIn)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: GalleryDetailPageIn,
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
        return withContext(Dispatchers.IO) {
            val targetTimestamp = System.currentTimeMillis() - DBValue.GALLERY_PREVIEW_CACHE_DURATION
            val data = RoomData.galleryDao.queryGalleryPreview(gid, token, index, targetTimestamp)
            if (data != null) {
                RequestResult.SuccessResult(GalleryPreview(data))
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
                                .apply {
                                    RoomData.galleryDao.insertGalleryPreview(
                                        GalleryPreviewCache(gid, token, index, this)
                                    )
                                }
                        )
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                } catch (inner: Throwable) {
                    RequestResult.FailResult(inner)
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
        return withContext(Dispatchers.IO) {
            val targetTimestamp = System.currentTimeMillis() - DBValue.IMAGE_SROUCE_CACHE_DURATION
            val cache = RoomData.galleryDao.querySingleGalleryImageCache(gid, token, index ,targetTimestamp)
            if (cache == null || cache.pToken.isEmpty()) {
                val remoteData = HttpRookie
                    .get<PageInfo<ImageSource>>(Url.galleryDetail(gid, token)) {
                        convert = ImageSourceConvert()
                        if (index != 0) urlParams(RequestKey.PAGE_DETAIL to index.toString())
                    }
                    .execute()
                try {
                    when (remoteData) {
                        is Response.Success<PageInfo<ImageSource>> -> {
                            remoteData.requireBody().also {
                                RoomData.galleryDao.insertGalleryImageSource(gid, token, it)
                            }

                            val pToken =
                                RoomData.galleryDao.querySingleGalleryImageCache(gid, token, index, targetTimestamp)
                                    ?.pToken

                            if (pToken.isNullOrEmpty()) throw IllegalStateException("not found pToken")
                            RequestResult.SuccessResult(pToken)
                        }
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                } catch (inner: Throwable) {
                    RequestResult.FailResult(inner)
                }
            } else RequestResult.SuccessResult(cache.pToken)
        }
    }

    override suspend fun login(account: String, password: String): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val loginData = HttpRookie
                .post<String>(Url.login) {
                    convert = LoginConvert()
                    params(RequestKey.REFERER to ParamValue.LOGIN_REFERER)
                    params(RequestKey.B to "")
                    params(RequestKey.BT to "")

                    params(RequestKey.USER_NAME to account)
                    params(RequestKey.PASS_WORD to password)
                    params(RequestKey.COOKIE_DATE to "1")
                    //params(RequestKey.PRIVACY to "1")

                    header(RequestKey.HEADER_ORIGIN to ParamValue.LOGIN_HEADER_ORIGIN)
                    header(RequestKey.HEADER_REFERER to ParamValue.LOGIN_HEADER_REFERER)
                }
                .execute()
            try {
                when (loginData) {
                    is Response.Success<String> -> RequestResult.SuccessResult(loginData.requireBody())
                    is Response.Fail<*> -> throw loginData.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult(inner)
            }
        }
    }


    override suspend fun rating(detail: GalleryDetail, rating: Float): RequestResult<RateBack> {
        return withContext(Dispatchers.IO) {
            val data = HttpRookie
                .post<RateBack>(Url.api) {
                    convert = RateBackConvert()
                    json(
                        RequestRateInfo(
                            apiUid = detail.info.apiUID,
                            apiKey = detail.info.apiKey,
                            galleryID = detail.info.gid.toString(),
                            token = detail.info.token,
                            rating = ceil(rating * 2).toInt()
                        ).toJson()
                    )
                    header(RequestKey.HEADER_ORIGIN to Url.currentDomain)
                    header(
                        RequestKey.HEADER_REFERER to Url.galleryDetail(
                            detail.info.gid,
                            detail.info.token
                        )
                    )
                }
                .execute()
            try {
                when (data) {
                    is Response.Success<RateBack> -> RequestResult.SuccessResult(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.FailResult(inner)
            }
        }


    }
}