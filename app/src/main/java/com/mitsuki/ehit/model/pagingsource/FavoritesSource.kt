package com.mitsuki.ehit.model.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mitsuki.armory.httprookie.HttpRookie
import com.mitsuki.armory.httprookie.request.urlParams
import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.const.RequestKey
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.convert.GalleryListWithFavoriteCountConvert
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.model.page.GeneralPageIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesSource(
    private val pageIn: FavouritePageIn,
    private val dataWrap: FavouriteCountWrap
) :
    PagingSource<Int, Gallery>() {

    private val mConvert by lazy { GalleryListWithFavoriteCountConvert() }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gallery> {
        val page = params.key ?: GeneralPageIn.START
        return try {
            withContext(Dispatchers.IO) {
                val data: Response<Pair<ArrayList<Gallery>, Array<Int>>> = HttpRookie
                    .get<Pair<ArrayList<Gallery>, Array<Int>>>(Url.favoriteList) {
                        convert = mConvert
                        urlParams(RequestKey.PAGE, page.toString())
                        pageIn.setGroup(this)
                    }
                    .execute()

                when (data) {
                    is Response.Success<Pair<ArrayList<Gallery>, Array<Int>>> -> {
                        val dataPair: Pair<ArrayList<Gallery>, Array<Int>> = data.requireBody()
                        val list = dataPair.first
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