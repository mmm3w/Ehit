package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.UrlParams
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.convert.GalleryListConvert
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.MyUrlParams
import com.mitsuki.ehit.model.entity.PageInfo
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExGalleryListSource(
    private val repository: Repository,
    private val pageIn: GalleryListPageIn,
) :
    PagingSource<UrlParams, Gallery>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<UrlParams>): LoadResult<UrlParams, Gallery> {
        val up = params.key
        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            withContext(Dispatchers.IO) {
                when (val data: RequestResult<PageInfo<Gallery>> =
                    repository.exGalleryListSource(pageIn, up)) {
                    is RequestResult.Success<PageInfo<Gallery>> -> {
                        pageIn.maxPage = data.data.totalPage
                        LoadResult.Page(
                            data = data.data.data,
                            prevKey = data.data.prevKey?.let {
                                MyUrlParams { urlParams("prev", it.toString()) }
                            },
                            nextKey = data.data.prevKey?.let {
                                MyUrlParams { urlParams("next", it.toString()) }
                            }
                        )
                    }
                    is RequestResult.Fail<*> -> throw data.throwable
                }
            }
        } catch (inner: Throwable) {
            LoadResult.Error(inner)
        }
    }

    override val jumpingSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<UrlParams, Gallery>): UrlParams? =
        pageIn.exUrlParams
}