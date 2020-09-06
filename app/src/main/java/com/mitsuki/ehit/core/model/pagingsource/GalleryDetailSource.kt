package com.mitsuki.ehit.core.model.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.being.okhttp.execute
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryDetailSource(
    private val gid: Long,
    private val token: String,
    private val pageIn: PageIn,
    private val detailSource: GalleryDetailWrap,
    private val requestProvider: RequestProvider
) : PagingSource<Int, ImageSource>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageSource> {
        val page = pageIn.replace(params.key ?: 0)
        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            withContext(Dispatchers.Default) {
                val res = requestProvider.galleryDetailRequest(gid, token, page).execute()
                val galleryDetail = GalleryDetail.parse(res?.body?.string())

                detailSource.partInfo = galleryDetail?.obtainOperating()
                detailSource.comment =
                    galleryDetail?.obtainComments() ?: GalleryDetailWrap.DefaultComment
                detailSource.tags = galleryDetail?.tagSet ?: GalleryDetailWrap.DefaultTags

                LoadResult.Page(
                    data = galleryDetail?.images?.data ?: arrayListOf(),
                    prevKey = galleryDetail?.images?.prevKey,
                    nextKey = galleryDetail?.images?.nextKey
                )
            }
        } catch (e: Exception) {
            // 捕获异常，返回一个Error
            LoadResult.Error(e)
        }
    }
}