package com.mitsuki.ehit.model.entity

data class ListPageKey(
    val isNext:Boolean,
    val key:Long,
    val seek:String? = null,
    val jump:String? = null
) {




}