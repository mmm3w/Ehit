package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.page.GalleryListPageIn
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfoNew
import com.mitsuki.ehit.model.entity.ListPageKey
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExGalleryListSource(
    private val repository: Repository,
    private val pageIn: GalleryListPageIn,
) : PagingSource<ListPageKey, Gallery>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(params: LoadParams<ListPageKey>): LoadResult<ListPageKey, Gallery> {
        return try {
            withContext(Dispatchers.IO) {
                when (val data: RequestResult<PageInfoNew<Gallery>> =
                    repository.exGalleryListSource(pageIn.targetUrl, pageIn.key, params.key)) {
                    is RequestResult.Success<PageInfoNew<Gallery>> -> {
                        LoadResult.Page(
                            data = data.data.data,
                            prevKey = data.data.prevKey?.let { ListPageKey(false, it) },
                            nextKey = data.data.nextKey?.let { ListPageKey(true, it) }
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

    override fun getRefreshKey(state: PagingState<ListPageKey, Gallery>): ListPageKey? {
        if (pageIn.prepKey == null) return null
        val page = state.anchorPosition?.run { state.closestPageToPosition(this) }
        return pageIn.mergePageKey(page?.nextKey, page?.prevKey)
    }
}