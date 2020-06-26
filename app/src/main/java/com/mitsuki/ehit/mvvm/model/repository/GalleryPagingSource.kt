package com.mitsuki.ehit.mvvm.model.repository

import androidx.paging.PagingSource
import com.mitsuki.ehit.mvvm.model.MainModel
import com.mitsuki.ehit.mvvm.model.entity.Gallery

class GalleryPagingSource(private val model: MainModel) : PagingSource<Int, Gallery>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Gallery> {
        val page = params.key ?: 0
        return try {
            // 如果成功加载，那么返回一个LoadResult.Page,如果失败就返回一个Error
            // Page里传进列表数据，以及上一页和下一页的页数,具体的是否最后一页或者其他逻辑就自行判断
            // 需要注意的是，如果是第一页，prevKey就传null，如果是最后一页那么nextKey也传null
            // 其他情况prevKey就是page-1，nextKey就是page+1
            val list = model.getMainList()
            LoadResult.Page(
                data = list,
                prevKey = if (page == 0) null else page - 1,
                nextKey = null
            )
        } catch (e: Exception) {
            // 捕获异常，返回一个Error
            LoadResult.Error(e)
        }
    }
}