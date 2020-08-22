package com.mitsuki.ehit.core.model.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import com.mitsuki.ehit.being.okhttp.RequestProvider
import com.mitsuki.ehit.being.okhttp.execute
import com.mitsuki.ehit.core.crutch.PageIn
import com.mitsuki.ehit.core.model.entity.Gallery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryListSource constructor(private val pageIn: PageIn, private val requestProvider: RequestProvider) :
    PagingSource<Int, Gallery>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gallery> {
        val page = pageIn.replace(params.key ?: 0)
        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1

            withContext(Dispatchers.Default) {
                val res = requestProvider.galleryListRequest(page).execute()
                val list = Gallery.parseListCoroutines(res?.body?.string())
                LoadResult.Page(
                    data = list,
                    prevKey = if (page <= 0) null else page - 1,
                    nextKey = if (list.size > 0) page + 1 else null
//                nextKey = null
                )
            }
        } catch (e: Exception) {
            // 捕获异常，返回一个Error
            LoadResult.Error(e)
        }
    }
}