package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.VolatileCache
import com.mitsuki.ehit.crutch.db.RoomData
import com.mitsuki.ehit.model.convert.GalleryDetailConvert
import com.mitsuki.ehit.model.convert.ImageSourceConvert
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GeneralPageIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryDetailSource(
    private val mGid: Long,
    private val mToken: String,
    private val mPageIn: GeneralPageIn,
    private val mDetailSource: GalleryDetailWrap
) : PagingSource<Int, ImageSource>() {

    private val mConvert by lazy { GalleryDetailConvert() }
    private val mJustImageConvert by lazy { ImageSourceConvert() }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageSource> {
        val page = params.key ?: GeneralPageIn.START

        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            withContext(Dispatchers.IO) {
                if (page == GeneralPageIn.START) {
                    var reObtain = false
                    val cacheData = RoomData.galleryDao.queryGalleryDetail(mGid, mToken)
                    if (cacheData == null) {
                        reObtain = true
                    } else {
                        cacheData.apply {
                            mDetailSource.partInfo = obtainOperating()
                            mDetailSource.comment = obtainComments()
                            mDetailSource.commentState = obtainCommentState()
                            mDetailSource.tags = tagGroup
                            mDetailSource.sourceDetail = this
                        }
                    }

                    var images = RoomData.galleryDao.queryGalleryImageSource(mGid, mToken, page)
                    if (images.isEmpty) reObtain = true

                    if (reObtain) {
                        val remoteData: Response<Pair<GalleryDetail, PageInfo<ImageSource>>> =
                            HttpRookie
                                .get<Pair<GalleryDetail, PageInfo<ImageSource>>>(
                                    Url.galleryDetail(mGid, mToken)
                                ) {
                                    convert = mConvert
                                    urlParams(RequestKey.PAGE_DETAIL, page.toString())
                                }
                                .execute()

                        when (remoteData) {
                            is Response.Success<Pair<GalleryDetail, PageInfo<ImageSource>>> -> {
                                remoteData.requireBody().also { result ->
                                    result.first.apply {
                                        mDetailSource.partInfo = obtainOperating()
                                       mDetailSource.comment = obtainComments()
                                        mDetailSource.commentState = obtainCommentState()
                                        mDetailSource.tags = tagGroup
                                        mDetailSource.sourceDetail = this
                                    }
                                    images = result.second

                                    RoomData.galleryDao.insertGalleryDetail(result.first)
                                    RoomData.galleryDao
                                        .insertGalleryImageSource(mGid, mToken, result.second)
                                }
                            }
                            is Response.Fail<*> -> throw remoteData.throwable
                        }
                    }
                    VolatileCache.galleryPageSize = images.data.size
                    LoadResult.Page(
                        data = images.data,
                        prevKey = images.prevKey,
                        nextKey = images.nextKey
                    )
                } else {
                    //只解析图片

                    var images = RoomData.galleryDao.queryGalleryImageSource(mGid, mToken, page)
                    if (images.isEmpty) {
                        val remoteData: Response<PageInfo<ImageSource>> =
                            HttpRookie
                                .get<PageInfo<ImageSource>>(
                                    Url.galleryDetail(
                                        mGid,
                                        mToken
                                    )
                                ) {
                                    convert = mJustImageConvert
                                    urlParams(RequestKey.PAGE_DETAIL, page.toString())
                                }
                                .execute()

                        when (remoteData) {
                            is Response.Success<PageInfo<ImageSource>> -> {
                                remoteData.requireBody().also { result ->
                                    images = result
                                    RoomData.galleryDao
                                        .insertGalleryImageSource(mGid, mToken, result)
                                }
                            }
                            is Response.Fail<*> -> throw remoteData.throwable
                        }
                    }
                    VolatileCache.galleryPageSize = images.data.size
                    LoadResult.Page(
                        data = images.data,
                        prevKey = images.prevKey,
                        nextKey = images.nextKey
                    )
                }
            }
        } catch (inner: Throwable) {
            // 捕获异常，返回一个Error
            LoadResult.Error(inner)
        }
    }

    override val jumpingSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, ImageSource>): Int = mPageIn.targetPage
}