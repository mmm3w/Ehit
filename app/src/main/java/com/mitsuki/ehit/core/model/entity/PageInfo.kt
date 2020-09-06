package com.mitsuki.ehit.core.model.entity

data class PageInfo<D>(
    val data: List<D>,
    val index: Int,
    val totalSize: Int,
    val totalPage: Int,//暂时未解析，也暂时没有用
    val prevKey: Int?,
    val nextKey: Int?
)