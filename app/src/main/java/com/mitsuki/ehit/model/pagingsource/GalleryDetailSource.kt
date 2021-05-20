package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.MemoryCache
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.PageIn
import com.mitsuki.ehit.model.convert.GalleryDetailConvert
import com.mitsuki.ehit.model.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryDetailSource(
    private val mGid: Long,
    private val mToken: String,
    private val mPageIn: PageIn,
    private val mDetailSource: GalleryDetailWrap
) : PagingSource<Int, ImageSource>() {

    private val mConvert by lazy { GalleryDetailConvert() }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageSource> {
        val page = params.key ?: 0

        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            withContext(Dispatchers.IO) {
                var data = MemoryCache.getGalleryDetail(mGid, page)?.apply {
                    if (page == 0) {
                        mDetailSource.partInfo = obtainOperating()
                        mDetailSource.comment = obtainComments()
                        mDetailSource.commentState = obtainCommentState()
                        mDetailSource.tags = tagSet
                        mDetailSource.sourceDetail = this
                    }
                }
                if (data == null) {
                    val remoteData: Response<GalleryDetail> =
                        HttpRookie
                            .get<GalleryDetail>(Url.galleryDetail(mGid, mToken)) {
                                convert = mConvert
                                urlParams(RequestKey.PAGE_DETAIL to page.toString())
                            }
                            .execute()

                    when (remoteData) {
                        is Response.Success<GalleryDetail> -> {
                            data = remoteData.requireBody().apply {
                                mDetailSource.partInfo = obtainOperating()
                                mDetailSource.comment = obtainComments()
                                mDetailSource.commentState = obtainCommentState()
                                mDetailSource.tags = tagSet
                                mDetailSource.sourceDetail = this


                                if (images.data.isNotEmpty()) {
                                    MemoryCache.detailPageSize =
                                        if (page == 0) images.data.size else images.data[0].index / images.index
                                }
                                MemoryCache.cacheImageToken(mGid, images.data)
                                MemoryCache.cacheGalleryDetail(mGid, page, this)
                            }
                        }
                        is Response.Fail<*> -> throw remoteData.throwable
                    }
                }

                LoadResult.Page(
                    data = data.images.data,
                    prevKey = data.images.prevKey,
                    nextKey = data.images.nextKey
                )
            }
        } catch (inner: Throwable) {
            // 捕获异常，返回一个Error
            LoadResult.Error(inner)
        }
    }
}