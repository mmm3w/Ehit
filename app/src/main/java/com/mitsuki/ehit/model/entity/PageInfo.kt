package com.mitsuki.ehit.model.entity

data class PageInfo<D>(
    val data: List<D>,
    val index: Int,
    val totalSize: Int,
    val totalPage: Int,
    val prevKey: Int?,
    val nextKey: Int?
) {
    companion object {
        fun <T> empty(): PageInfo<T> {
            return PageInfo(arrayListOf(), 0, 0, 0, null, null)
        }
    }

    val isEmpty get() = data.isEmpty()
}