package com.mitsuki.ehit.model.repository.impl

import android.webkit.MimeTypeMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mitsuki.armory.httprookie.convert.FileConvert
import com.mitsuki.armory.httprookie.convert.StringConvert
import com.mitsuki.armory.httprookie.get
import com.mitsuki.armory.httprookie.post
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
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.VolatileCache
import com.mitsuki.ehit.crutch.di.ApiClient
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
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.pagingsource.PagingSource
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject
import kotlin.math.ceil

class RepositoryImpl @Inject constructor(
    val galleryDao: GalleryDao,
    val downloadDao: DownloadDao,
    val pagingProvider: PagingSource,
    @ApiClient val client: OkHttpClient
) : Repository {

    //pageconfig
    private val mListPagingConfig by lazy { PagingConfig(pageSize = 25) }
    private val mDetailPagingConfig by lazy { PagingConfig(pageSize = 40) }
    private val mFavoritePagingConfig by lazy { PagingConfig(pageSize = 50) }

    //convert
    private val mGalleryListConvert by lazy { GalleryListConvert() }
    private val mGalleryDetailImageConvert by lazy { GalleryDetailImageConvert() }
    private val mJustImageConvert by lazy { ImageSourceConvert() }

    private val mFavoritesSourceConvert by lazy { GalleryListWithFavoriteCountConvert() }

    override fun galleryList(pageIn: GalleryListPageIn): Flow<PagingData<Gallery>> {
        return Pager(mListPagingConfig, initialKey = GeneralPageIn.START) {
            pagingProvider.galleryListSource(this, pageIn)
        }.flow
    }

    override fun detailImage(
        gid: Long,
        token: String,
        pageIn: GeneralPageIn
    ): Flow<PagingData<ImageSource>> {
        return Pager(mDetailPagingConfig, initialKey = GeneralPageIn.START) {
            pagingProvider.detailImageSource(this, gid, token, pageIn)
        }.flow
    }


    override suspend fun login(account: String, password: String): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val data = client
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
                    pageIn.attachPage(this, page)
                    pageIn.attachSearchKey(this)
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
                        Url.galleryDetail(gid, token)
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
                        VolatileCache.galleryPageSize = result.second.data.size
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
                PageInfo.emtpy()
            else
                galleryDao.queryGalleryImageSource(gid, token, page)

            if (images.isEmpty) {
                val remoteData: Response<PageInfo<ImageSource>> =
                    client
                        .get<PageInfo<ImageSource>>(Url.galleryDetail(gid, token)) {
                            convert = mJustImageConvert
                            urlParams(RequestKey.PAGE_DETAIL, page.toString())
                        }
                        .execute()
                when (remoteData) {
                    is Response.Success<PageInfo<ImageSource>> -> {
                        remoteData.requireBody().run {
                            VolatileCache.galleryPageSize = data.size
                            galleryDao.insertGalleryImageSource(gid, token, this)
                            RequestResult.Success(this)
                        }
                    }
                    is Response.Fail<*> -> RequestResult.Fail(remoteData.throwable)
                }
            } else {
                VolatileCache.galleryPageSize = images.data.size
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
            val data = client.post<RateBack>(Url.api) {
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
                header(RequestKey.HEADER_ORIGIN, Url.currentDomain)
                header(
                    RequestKey.HEADER_REFERER, Url.galleryDetail(gid, token)
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

    override suspend fun galleryPreview(
        gid: Long,
        token: String,
        index: Int
    ): RequestResult<GalleryPreview> {
        return withContext(Dispatchers.IO) {
            try {
                val cache =
                    galleryDao.querySingleGalleryImageCache(gid, token, index)
                val pToken = if (cache == null || cache.pToken.isEmpty()) {
                    val webIndex =
                        if (VolatileCache.galleryPageSize == 0) index else index / VolatileCache.galleryPageSize

                    val images = galleryImageSource(gid, token, webIndex, true)
                    if (images is RequestResult.Fail<*>) {
                        throw images.throwable
                    }

                    galleryDao.querySingleGalleryImageCache(gid, token, index)?.pToken?.apply {
                        if (isEmpty()) throw IllegalStateException("request pToken error")
                    }
                        ?: throw IllegalStateException("request pToken error")
                } else {
                    cache.pToken
                }

                val data = galleryDao.queryGalleryPreview(gid, token, index)
                if (data != null) {
                    RequestResult.Success(GalleryPreview(data))
                } else {
                    val remoteData: Response<GalleryPreview> =
                        client.get<GalleryPreview>(Url.galleryPreviewDetail(gid, pToken, index)) {
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
                }

            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }


    override fun favoriteList(
        pageIn: FavouritePageIn,
        dataWrap: FavouriteCountWrap
    ): Flow<PagingData<Gallery>> {
        return Pager(mFavoritePagingConfig, initialKey = GeneralPageIn.START) {
            pagingProvider.favoritesSource(this, pageIn, dataWrap)
        }.flow
    }


    override suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String> {
        return withContext(Dispatchers.IO) {
            val data = client.post<String>(Url.favorites) {
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
                    is Response.Success<String> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {

                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun galleryComment(gid: Long, token: String, allComment: Boolean)
            : RequestResult<List<Comment>> {
        return withContext(Dispatchers.IO) {
            val data = client.get<List<Comment>>(Url.galleryDetail(gid, token)) {
                convert = GalleryCommentsConvert()
                if (allComment) urlParams(RequestKey.HC, "1")
            }
                .execute()
            try {
                when (data) {
                    is Response.Success<List<Comment>> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }

    override suspend fun sendGalleryComment(
        gid: Long,
        token: String,
        comment: String
    ): RequestResult<Int> = withContext(Dispatchers.IO) {
        val data = client.post<Int>(Url.galleryDetail(gid, token)) {
            convert = SendCommentConvert()
            urlParams(RequestKey.HC, "1")
            params(RequestKey.COMMENT_TEXT, comment)

            header(RequestKey.HEADER_ORIGIN, Url.currentDomain)
            header(RequestKey.HEADER_REFERER, url())
        }
            .execute()

        try {
            when (data) {
                is Response.Success<Int> -> RequestResult.Success(0)
                is Response.Fail<*> -> throw  data.throwable
            }
        } catch (inner: Throwable) {
            RequestResult.Fail(inner)
        }
    }


    override suspend fun voteGalleryComment(
        apiKey: String,
        apiUid: Long,
        gid: Long,
        token: String,
        cid: Long,
        vote: Int
    ): RequestResult<VoteBack> =
        withContext(Dispatchers.IO) {
            val data = client.post<VoteBack>(Url.api) {
                convert = VoteBackConvert()
                json(
                    RequestVoteInfo(
                        apiUid = apiUid,
                        apiKey = apiKey,
                        galleryID = gid,
                        token = token,
                        cid = cid,
                        vote = vote
                    ).toJson()
                )
                header(RequestKey.HEADER_ORIGIN, Url.currentDomain)
                header(RequestKey.HEADER_REFERER, Url.galleryDetail(gid, token))
            }
                .execute()
            try {
                when (data) {
                    is Response.Success<VoteBack> -> RequestResult.Success(data.requireBody())
                    is Response.Fail<*> -> throw data.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }

    override suspend fun downloadThumb(gid: Long, token: String): RequestResult<File> =
        withContext(Dispatchers.IO) {
            downloadDao.queryDownloadInfo(gid, token)?.run {
                val downloadUrl = thumb
                val folder = AppHolder.cacheDir("thumb")
                val name = "thumb_${gid}_$token.${MimeTypeMap.getFileExtensionFromUrl(downloadUrl)}"
                val thumbFile = File(folder, name)
                if (thumbFile.exists()) {
                    RequestResult.Success(thumbFile)
                } else {
                    downloadFile(downloadUrl, folder, name)
                }
            } ?: RequestResult.Fail(IllegalAccessException("not found info"))
        }

    override suspend fun downloadPage(gid: Long, token: String, index: Int): RequestResult<File> =
        withContext(Dispatchers.IO) {
            when (val info: RequestResult<GalleryPreview> = galleryPreview(gid, token, index)) {
                is RequestResult.Success<GalleryPreview> -> {
                    val cacheFolder = AppHolder.cacheDir("download/$gid-$token")
                    val fileName = String.format("%09d", index) + "." +
                            MimeTypeMap.getFileExtensionFromUrl(info.data.imageUrl)
                    val imageFile = File(cacheFolder, fileName)
                    if (imageFile.exists()) {
                        RequestResult.Success(imageFile)
                    } else {
                        downloadFile(info.data.imageUrl, cacheFolder, fileName)
                    }
                }
                is RequestResult.Fail<*> -> {
                    RequestResult.Fail(info.throwable)
                }
            }
        }


    override suspend fun downloadFile(
        url: String,
        folder: File,
        name: String
    ): RequestResult<File> = withContext(Dispatchers.IO) {
        if (folder.exists() && folder.isFile) {
            RequestResult.Fail(IllegalAccessException("can not create folder"))
        } else {
            if (!folder.exists()) folder.mkdirs()
            val result = client.get<File>(url) {
                convert = FileConvert(folder.absolutePath, name)
            }.execute()

            try {
                when (result) {
                    is Response.Success<File> -> RequestResult.Success(result.requireBody())
                    is Response.Fail<*> -> throw result.throwable
                }
            } catch (inner: Throwable) {
                RequestResult.Fail(inner)
            }
        }
    }


    override suspend fun favoritesSource(
        pageIn: FavouritePageIn,
        page: Int
    ): Response<Pair<PageInfo<Gallery>, Array<Int>>> {
        return client
            .get<Pair<PageInfo<Gallery>, Array<Int>>>(Url.favoriteList) {
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
            .get<Pair<GalleryDetail, PageInfo<ImageSource>>>(Url.galleryDetail(mGid, mToken)) {
                convert = mGalleryDetailImageConvert
                urlParams(RequestKey.PAGE_DETAIL, page.toString())
            }
            .execute()
    }


}