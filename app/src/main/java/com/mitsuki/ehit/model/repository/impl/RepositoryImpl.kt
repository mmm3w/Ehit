package com.mitsuki.ehit.model.repository.impl

import android.webkit.MimeTypeMap
import coil.Coil
import com.mitsuki.armory.httprookie.convert.FileConvert
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.get
import com.mitsuki.armory.httprookie.post
import com.mitsuki.armory.httprookie.request.header
import com.mitsuki.armory.httprookie.request.json
import com.mitsuki.armory.httprookie.request.params
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.const.DirManager
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.crutch.network.Site
import com.mitsuki.ehit.crutch.toJson
import com.mitsuki.ehit.const.ParamsValue
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.save.ShareData
import com.mitsuki.ehit.model.convert.*
import com.mitsuki.ehit.model.dao.DownloadDao
import com.mitsuki.ehit.model.dao.GalleryDao
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.db.GalleryPreviewCache
import com.mitsuki.ehit.model.entity.reponse.RateBack
import com.mitsuki.ehit.model.entity.reponse.VoteBack
import com.mitsuki.ehit.model.entity.request.RequestRateInfo
import com.mitsuki.ehit.model.entity.request.RequestVoteInfo
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject
import kotlin.math.ceil

class RepositoryImpl @Inject constructor(
    val galleryDao: GalleryDao,
    val downloadDao: DownloadDao,
    val client: OkHttpClient,
    val shareData: ShareData
) : Repository {

    private var galleryPageSize: Int = shareData.spGalleryPageSize
        set(value) {
            if (value != field) {
                shareData.spGalleryPageSize = value
                field = value
            }
        }

    //convert
    private val mGalleryListConvert by lazy {
        GalleryListConvert()
            .decoIPBanned()
            .deco302()
    }
    private val mGalleryDetailImageConvert by lazy { GalleryDetailImageConvert() }
    private val mJustImageConvert by lazy { ImageSourceConvert() }

    private val mFavoritesSourceConvert by lazy { GalleryListWithFavoriteCountConvert() }

    override suspend fun login(account: String, password: String): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val data = client
                .post<String>(Site.login) {
                    convert = LoginConvert()
                    params(RequestKey.REFERER, ParamsValue.LOGIN_REFERER)
                    params(RequestKey.B, "")
                    params(RequestKey.BT, "")

                    params(RequestKey.USER_NAME, account)
                    params(RequestKey.PASS_WORD, password)
                    params(RequestKey.COOKIE_DATE, "1")
                    //params(RequestKey.PRIVACY to "1")

                    header(RequestKey.HEADER_ORIGIN, ParamsValue.LOGIN_HEADER_ORIGIN)
                    header(RequestKey.HEADER_REFERER, ParamsValue.LOGIN_HEADER_REFERER)
                }
                .execute()

            when (data) {
                is Response.Success<String> -> RequestResult.Success(data.requireBody())
                is Response.Fail<*> -> RequestResult.Fail(data.throwable)
            }
        }
    }

    override suspend fun galleryListSource(
        pageIn: GalleryListPageIn,
        page: Int
    ): RequestResult<PageInfo<Gallery>> {
        return withContext(Dispatchers.IO) {
            val data = client
                .get<PageInfo<Gallery>>(pageIn.targetUrl) {
                    convert = mGalleryListConvert
                    pageIn.attachParams(this, page)
                }
                .execute()

            when (data) {
                is Response.Success<PageInfo<Gallery>> -> RequestResult.Success(data.requireBody())
                is Response.Fail<*> -> RequestResult.Fail(data.throwable)
            }
        }
    }

    override suspend fun galleryDetailInfo(gid: Long, token: String): RequestResult<GalleryDetail> {
        return withContext(Dispatchers.IO) {
            //首先从数据库缓存中读取相关数据
            val cacheData = galleryDao.queryGalleryDetail(gid, token)
            if (cacheData != null) {
                RequestResult.Success(cacheData)
            } else {
                val data = client
                    .get<Pair<GalleryDetail, PageInfo<ImageSource>>>(
                        Site.galleryDetail(gid, token)
                    ) {
                        convert = mGalleryDetailImageConvert
                    }
                    .execute()
                when (data) {
                    is Response.Success<Pair<GalleryDetail, PageInfo<ImageSource>>> -> {
                        val result = data.requireBody()
                        galleryDao.insertGalleryDetail(result.first)
                        galleryDao
                            .insertGalleryImageSource(gid, token, result.second)
                        galleryPageSize = result.second.data.size
                        RequestResult.Success(result.first)
                    }
                    is Response.Fail<*> -> RequestResult.Fail(data.throwable)
                }
            }
        }
    }

    override suspend fun galleryImageSource(
        gid: Long,
        token: String,
        page: Int,
        ignoreCache: Boolean
    ): RequestResult<PageInfo<ImageSource>> {
        return withContext(Dispatchers.IO) {
            val images = if (ignoreCache)
                PageInfo.empty()
            else
                galleryDao.queryGalleryImageSource(gid, token, page)

            if (images.isEmpty) {
                val remoteData: Response<PageInfo<ImageSource>> =
                    client
                        .get<PageInfo<ImageSource>>(Site.galleryDetail(gid, token)) {
                            convert = mJustImageConvert
                            urlParams(RequestKey.PAGE_DETAIL, page.toString())
                        }
                        .execute()
                when (remoteData) {
                    is Response.Success<PageInfo<ImageSource>> -> {
                        remoteData.requireBody().run {
                            galleryPageSize = data.size
                            galleryDao.insertGalleryImageSource(gid, token, this)
                            RequestResult.Success(this)
                        }
                    }
                    is Response.Fail<*> -> RequestResult.Fail(remoteData.throwable)
                }
            } else {
                galleryPageSize = images.data.size
                RequestResult.Success(images)
            }
        }
    }

    override suspend fun rating(
        gid: Long,
        token: String,
        apiUid: Long,
        apiKey: String, rating: Float
    ): RequestResult<RateBack> {
        return withContext(Dispatchers.IO) {
            val data = client.post<RateBack>(Site.api) {
                convert = RateBackConvert()
                json(
                    RequestRateInfo(
                        apiUid = apiUid,
                        apiKey = apiKey,
                        galleryID = gid.toString(),
                        token = token,
                        rating = ceil(rating * 2).toInt()
                    ).toJson()
                )
                header(RequestKey.HEADER_ORIGIN, Site.currentDomain)
                header(
                    RequestKey.HEADER_REFERER, Site.galleryDetail(gid, token)
                )
            }
                .execute()
            try {
                when (data) {
                    is Response.Success<RateBack> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {

                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun galleryImagePToken(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                //先尝试从缓存中读取
                val cache =
                    galleryDao.querySingleGalleryImageCache(gid, token, index)
                val pToken = if (cache == null || cache.pToken.isEmpty()) {
                    //缓存中没有时请求网络
                    if (galleryPageSize == 0) {
                        galleryImageSource(gid, token, 0, true)
                    }
                    galleryDao.querySingleGalleryImageCache(gid, token, index)?.pToken ?: let {
                        if (galleryPageSize == 0) throw IllegalStateException("request page size error")
                        val webIndex = index / galleryPageSize

                        val images = galleryImageSource(gid, token, webIndex, true)
                        if (images is RequestResult.Fail<*>) {
                            throw images.throwable
                        }

                        galleryDao.querySingleGalleryImageCache(gid, token, index)?.pToken?.apply {
                            if (isEmpty()) throw IllegalStateException("request pToken error")
                        } ?: throw IllegalStateException("request pToken error")
                    }

                } else {
                    cache.pToken
                }
                RequestResult.Success(pToken)
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun galleryPreview(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.IO) {
            try {
                val pToken = galleryImagePToken(gid, token, index).let {
                    when (it) {
                        is RequestResult.Success<String> -> it.data
                        is RequestResult.Fail<*> -> throw it.throwable
                    }
                }

                val data = galleryDao.queryGalleryPreview(gid, token, index)
                if (data != null) {
                    RequestResult.Success(GalleryPreview(data))
                } else {
                    galleryPreview(Site.galleryPreviewDetail(gid, pToken, index), gid, token, index)
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun galleryPreview(
        retryUrl: String,
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.IO) {
            try {
                val remoteData: Response<GalleryPreview> =
                    client.get<GalleryPreview>(retryUrl) {
                        convert = GalleryPreviewConvert()
                    }.execute()

                when (remoteData) {
                    is Response.Success<GalleryPreview> -> RequestResult.Success(
                        remoteData.requireBody()
                            .apply {
                                galleryDao.insertGalleryPreview(
                                    GalleryPreviewCache(gid, token, index, this)
                                )
                            }
                    )
                    is Response.Fail<*> -> throw remoteData.throwable
                }

            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }


    override suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val data = client.post<String>(Site.favorites) {
                convert = StringConvert()
                urlParams(RequestKey.GID, gid.toString())
                urlParams(RequestKey.T, token)
                urlParams(RequestKey.ACT, ParamsValue.ACT_FAVORITE)

                params(
                    RequestKey.FAVORITE_KEY_CAT,
                    if (cat < 0) ParamsValue.FAVORITE_VALUE_CAT_DEL else cat.toString()
                )
                params(RequestKey.FAVORITE_KEY_NOTE, ParamsValue.FAVORITE_VALUE_NOTE)
                params(RequestKey.FAVORITE_KEY_APPLY, ParamsValue.FAVORITE_VALUE_APPLY_APPLY)
                params(RequestKey.FAVORITE_KEY_UPDATE, ParamsValue.FAVORITE_VALUE_UPDATE)

                header(RequestKey.HEADER_ORIGIN, Site.currentDomain)
                header(RequestKey.HEADER_REFERER, url())
            }
                .execute()

            try {
                when (data) {
                    is Response.Success<String> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {

                RequestResult.Fail(inner)
            }
        }
    }

    override fun queryGalleryName(gid: Long, token: String): String {
        return downloadDao.queryDownloadInfo(gid, token)?.title ?: ""
    }

    override suspend fun favoritesSource(
        pageIn: FavouritePageIn,
        page: Int
    ): Response<Pair<PageInfo<Gallery>, Array<Int>>> {
        return client
            .get<Pair<PageInfo<Gallery>, Array<Int>>>(Site.favoriteList) {
                convert = mFavoritesSourceConvert
                urlParams(RequestKey.PAGE, page.toString())
                pageIn.setGroup(this)
            }
            .execute()
    }

    override suspend fun galleryDetailSource(
        mGid: Long,
        mToken: String,
        page: Int
    ): Response<Pair<GalleryDetail, PageInfo<ImageSource>>> {
        return client
            .get<Pair<GalleryDetail, PageInfo<ImageSource>>>(Site.galleryDetail(mGid, mToken)) {
                convert = mGalleryDetailImageConvert
                urlParams(RequestKey.PAGE_DETAIL, page.toString())
            }
            .execute()
    }


}