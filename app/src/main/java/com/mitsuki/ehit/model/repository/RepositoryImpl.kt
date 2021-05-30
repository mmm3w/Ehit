package com.mitsuki.ehit.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.json
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.crutch.toJson
import com.mitsuki.ehit.const.ParamValue
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.Log
import com.mitsuki.ehit.crutch.VolatileCache
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.convert.GalleryPreviewConvert
import com.mitsuki.ehit.model.convert.ImageSourceConvert
import com.mitsuki.ehit.model.convert.LoginConvert
import com.mitsuki.ehit.model.convert.RateBackConvert
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.db.GalleryPreviewCache
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.pagingsource.FavoritesSource
import com.mitsuki.ehit.model.pagingsource.GalleryDetailSource
import com.mitsuki.ehit.model.pagingsource.GalleryListSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

class RepositoryImpl @Inject constructor() : Repository {

    private val mListPagingConfig =
        PagingConfig(pageSize = 25)

    private val mDetailPagingConfig =
        PagingConfig(pageSize = 40)

    private val mFavoritePagingConfig =
        PagingConfig(pageSize = 50)

    override fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig, initialKey = GeneralPageIn.START) {
            GalleryListSource(pageIn)
        }.flow
    }

    override fun galleryDetail(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn,
        detailSource: GalleryDetailWrap
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = GeneralPageIn.START) {
            GalleryDetailSource(gid, token, pageIn, detailSource)
        }.flow
    }

    override fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>> {
        return Pager(mFavoritePagingConfig, initialKey = GeneralPageIn.START) {
            FavoritesSource(pageIn, dataWrap)
        }.flow
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun galleryPreview(
        gid: Long,
        token: String,
        pToken: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.IO) {
            val data = RoomData.galleryDao.queryGalleryPreview(gid, token, index)
            if (data != null) {
                RequestResult.SuccessResult(GalleryPreview(data))
            } else {
                val remoteData: Response<GalleryPreview> = HttpRookie
                    .get<GalleryPreview>(Url.galleryPreviewDetail(gid, pToken, index)) {
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
                    Log.debug("$inner")
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
            val cache =
                RoomData.galleryDao.querySingleGalleryImageCache(gid, token, index)
            if (cache == null || cache.pToken.isEmpty()) {
                val webIndex =
                    if (VolatileCache.galleryPageSize == 0) index else index / VolatileCache.galleryPageSize

                val remoteData = HttpRookie
                    .get<PageInfo<ImageSource>>(Url.galleryDetail(gid, token)) {
                        convert = ImageSourceConvert()
                        urlParams(RequestKey.PAGE_DETAIL, webIndex.toString())
                    }
                    .execute()
                try {
                    when (remoteData) {
                        is Response.Success<PageInfo<ImageSource>> -> {
                            remoteData.requireBody().also {
                                VolatileCache.galleryPageSize = it.data.size
                                RoomData.galleryDao.insertGalleryImageSource(gid, token, it)
                            }

                            val pToken =
                                RoomData.galleryDao.querySingleGalleryImageCache(
                                    gid,
                                    token,
                                    index
                                )
                                    ?.pToken

                            if (pToken.isNullOrEmpty()) throw IllegalStateException("not found pToken")
                            RequestResult.SuccessResult(pToken)
                        }
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                } catch (inner: Throwable) {
                    Log.debug("$inner")
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
                    params(RequestKey.REFERER, ParamValue.LOGIN_REFERER)
                    params(RequestKey.B, "")
                    params(RequestKey.BT, "")

                    params(RequestKey.USER_NAME, account)
                    params(RequestKey.PASS_WORD, password)
                    params(RequestKey.COOKIE_DATE, "1")
                    //params(RequestKey.PRIVACY to "1")

                    header(RequestKey.HEADER_ORIGIN, ParamValue.LOGIN_HEADER_ORIGIN)
                    header(RequestKey.HEADER_REFERER, ParamValue.LOGIN_HEADER_REFERER)
                }
                .execute()
            try {
                when (loginData) {
                    is Response.Success<String> -> RequestResult.SuccessResult(loginData.requireBody())
                    is Response.Fail<*> -> throw loginData.throwable
                }
            } catch (inner: Throwable) {
                Log.debug("$inner")
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
                    header(RequestKey.HEADER_ORIGIN, Url.currentDomain)
                    header(
                        RequestKey.HEADER_REFERER, Url.galleryDetail(
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
                Log.debug("$inner")
                RequestResult.FailResult(inner)
            }
        }


    }

    override suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val data = HttpRookie
                .post<String>(Url.favorites) {
                    convert = StringConvert()
                    urlParams(RequestKey.GID, gid.toString())
                    urlParams(RequestKey.T, token)
                    urlParams(RequestKey.ACT, ParamValue.ACT_FAVORITE)

                    params(
                        RequestKey.FAVORITE_KEY_CAT,
                        if (cat < 0) ParamValue.FAVORITE_VALUE_CAT_DEL else cat.toString()
                    )
                    params(RequestKey.FAVORITE_KEY_NOTE, ParamValue.FAVORITE_VALUE_NOTE)
                    params(RequestKey.FAVORITE_KEY_APPLY, ParamValue.FAVORITE_VALUE_APPLY_APPLY)
                    params(RequestKey.FAVORITE_KEY_UPDATE, ParamValue.FAVORITE_VALUE_UPDATE)

                    header(RequestKey.HEADER_ORIGIN, Url.currentDomain)
                    header(RequestKey.HEADER_REFERER, url())
                }
                .execute()

            try {
                when (data) {
                    is Response.Success<String> -> RequestResult.SuccessResult(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                Log.debug("$inner")
                RequestResult.FailResult(inner)
            }
        }
    }
}