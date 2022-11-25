package com.mitsuki.ehit.model.entity

data class NewPageInfo<D, K>(
    val data: List<D>,
    val prevKey: K?,
    val nextKey: K?
) {
    val isEmpty get() = data.isEmpty()
}