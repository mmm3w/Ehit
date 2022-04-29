package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.PageInfo
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import com.mitsuki.ehit.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesSource(
    private val repository: Repository,
    private val pageIn: FavouritePageIn,
    private val dataWrap: FavouriteCountWrap
) :
    PagingSource<Int, Gallery>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gallery> {
        val page = params.key ?: GeneralPageIn.START
        return try {
            withContext(Dispatchers.IO) {
                when (val data: Response<Pair<PageInfo<Gallery>, Array<Int>>> =
                    repository.favoritesSource(pageIn, page)) {
                    is Response.Success<Pair<PageInfo<Gallery>, Array<Int>>> -> {
                        val dataPair: Pair<PageInfo<Gallery>, Array<Int>> = data.requireBody()
                        val list = dataPair.first.data
                        val countData = dataPair.second
                        dataWrap.postData(GalleryFavorites.attachName(countData))
                        LoadResult.Page(
                            data = list,
                            prevKey = if (page <= GeneralPageIn.START) null else page - 1,
                            nextKey = if (list.isNotEmpty()) page + 1 else null
                        )
                    }
                    is Response.Fail<*> -> throw data.throwable
                }
            }
        } catch (inner: Throwable) {
            LoadResult.Error(inner)
        }
    }

    override val jumpingSupported: Boolean = true

    override fun getRefreshKey(state: PagingState<Int, Gallery>): Int = pageIn.targetPage
}