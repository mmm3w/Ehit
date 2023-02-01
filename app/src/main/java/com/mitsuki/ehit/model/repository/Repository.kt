package com.mitsuki.ehit.model.repository

import com.mitsuki.armory.httprookie.response.Response
import com.mitsuki.ehit.crutch.network.RequestResult
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.reponse.RateBack
import com.mitsuki.ehit.model.page.FavouritePageIn

interface Repository {

    suspend fun login(account: String, password: String): RequestResult<String>

    //外部列表的数据
    suspend fun exGalleryListSource(
        target: String, key: GalleryDataKey?, pageKey: ListPageKey?
    ): RequestResult<PageInfoNew<Gallery>>

    //获取画廊的详情
    //这一步的数据需要一步缓存
    //这一步解析详情和第一页的图
    suspend fun getGalleryDetailInfo(gid: Long, token: String): RequestResult<GalleryDetail>

    //获取画廊详情底下的图片
    //第一页在请求详情的时候缓存过，直接从缓存中读取，后面的再请求数据
    //ImageSource缓存是关键，包括单页中的查询
    suspend fun getGalleryDetailImageSource(
        gid: Long, token: String, page: Int, ignoreCache: Boolean = false
    ): RequestResult<PageInfo<ImageSource>>

    suspend fun rating(
        gid: Long, token: String, apiUid: Long, apiKey: String, rating: Float
    ): RequestResult<RateBack>

    suspend fun galleryImagePToken(gid: Long, token: String, index: Int): RequestResult<String>

    suspend fun getGalleryPreviewInfo(
        gid: Long,
        token: String,
        index: Int,
        reloadKey: String = ""
    ): RequestResult<GalleryPreview>

    suspend fun galleryPreview(
        gid: Long,
        token: String,
        index: Int,
        reloadKey: String = ""
    ): RequestResult<GalleryPreview>


    suspend fun galleryPreview(
        url: String, gid: Long, token: String, index: Int, reloadKey: String = ""
    ): RequestResult<GalleryPreview>

    suspend fun favorites(gid: Long, token: String, cat: Int): RequestResult<String>

    suspend fun favoritesSource(
        pageIn: FavouritePageIn, page: Int
    ): Response<Pair<PageInfo<Gallery>, Array<Int>>>

    suspend fun galleryDetailSource(
        mGid: Long, mToken: String, page: Int
    ): Response<Pair<GalleryDetail, PageInfo<ImageSource>>>

    fun queryGalleryName(gid: Long, token: String): String
}