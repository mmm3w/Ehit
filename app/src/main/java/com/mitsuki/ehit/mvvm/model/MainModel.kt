package com.mitsuki.ehit.mvvm.model

import com.mitsuki.ehit.being.Url
import com.mitsuki.ehit.mvvm.model.ehparser.GalleryListParser
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.model.okhttp.HttpRookie
import com.mitsuki.ehit.mvvm.model.okhttp.fakeHeader
import com.mitsuki.mvvm.base.BaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import javax.inject.Inject

class MainModel @Inject constructor() : BaseModel() {

    suspend fun getMainList() = withContext(Dispatchers.IO) {
        val list = ArrayList<Gallery>()
        val response =
            HttpRookie.client.newCall(Request.Builder().url(Url.list()).fakeHeader().build())
                .execute()
        list.addAll(GalleryListParser.parse(response.body?.string()!!))
        list
    }
}