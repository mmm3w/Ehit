package com.mitsuki.ehit.mvvm.model.pagingsource

import androidx.paging.PagingSource
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.being.okhttp.execute
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetail
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailItem
import com.mitsuki.ehit.mvvm.model.entity.GalleryDetailWrap

class GalleryDetailSource(
    private val gid: Long, private val token: String,
    private val requestProvider: RequestProvider
) :
    PagingSource<Int, GalleryDetailItem>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryDetailItem> {
        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            val res = requestProvider.galleryDetailRequest(gid, token).execute()
            val galleryDetail = GalleryDetail.parseCoroutines(res?.body?.string())
            val detailItem = GalleryDetailWrap.parseCoroutines(galleryDetail)
            LoadResult.Page(
                data = detailItem,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            // 捕获异常，返回一个Error
            LoadResult.Error(e)
        }
    }
}