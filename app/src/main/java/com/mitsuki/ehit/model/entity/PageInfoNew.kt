package com.mitsuki.ehit.model.entity

//还有个range需要处理
data class PageInfoNew<D>(
    val data: List<D>, //单页数据
    val totalSize: Int, //结果总数，不一定能得到，存在many的情况
    val totalDescription: String, //结果数的描述，暂时无用
    val prevKey: Long?, //前一页页码
    val nextKey: Long?   //后一页页码，需要包含近字段
) {

    val isEmpty get() = data.isEmpty()
}