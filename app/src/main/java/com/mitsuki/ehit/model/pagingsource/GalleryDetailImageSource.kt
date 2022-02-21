package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryDetailImageSource(
    private val repository: Repository,
    private val mGid: Long,
    private val mToken: String,
    private val mPageIn: GeneralPageIn
) : PagingSource<Int, ImageSource>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageSource> {
        val page = params.key ?: GeneralPageIn.START

        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            withContext(Dispatchers.IO) {
                var data = repository.galleryImageSource(mGid, mToken, page)
                when (data) {
                    is RequestResult.Success<PageInfo<ImageSource>> -> {
                        LoadResult.Page(
                            data = data.data.data,
                            prevKey = data.data.prevKey,
                            nextKey = data.data.nextKey
                        )
                    }
                    is RequestResult.Fail<*> -> throw data.throwable
                }
            }
        } catch (inner: Throwable) {
            // 捕获异常，返回一个Error
            LoadResult.Error(Throwable("gallery: $mGid-$mToken", inner))
        }
    }

    override val jumpingSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, ImageSource>): Int = mPageIn.targetPage
}