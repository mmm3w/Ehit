package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.*
import com.mitsuki.ehit.model.entity.db.*

@Dao
abstract class GalleryDao {

    @Transaction
    open suspend fun insertGalleryDetail(data: GalleryDetail) {
        insertGalleryInfo(data.info)
        insertGalleryTags(data.tagCache)
        insertGalleryComments(data.commentCache)
    }

    @Transaction
    open suspend fun queryGalleryDetail(
        gid: Long,
        token: String,
        careCacheTime: Boolean = true
    ): GalleryDetail? {
        val info = queryGalleryInfo(gid, token) ?: return null
        if (careCacheTime && System.currentTimeMillis() - info.timestamp > DBValue.INFO_CACHE_DURATION) return null

        val tags = queryGalleryTags(gid, token).groupBy { it.group }.map { map ->
            TagGroup(map.key, map.value.map { it.name }.toTypedArray())
        }.toTypedArray()
        val comments = queryGalleryComments(gid, token).map { cache ->
            Comment(cache.cid, cache.time, cache.user, cache.content)
        }.toTypedArray()

        return GalleryDetail(info, tags, comments)
    }

    @Transaction
    open suspend fun insertGalleryImageSource(
        gid: Long,
        token: String,
        imageData: PageInfo<ImageSource>
    ) {
        val currentTime = System.currentTimeMillis()
        imageData.data.map {
            GalleryImageSourceCache(
                gid,
                token,
                imageData.index,
                imageData.prevKey,
                imageData.nextKey,
                it,
                currentTime
            )
        }.apply { insertGalleryImageCache(this) }
    }

    @Transaction
    open suspend fun queryGalleryImageSource(
        gid: Long,
        token: String,
        index: Int
    ): PageInfo<ImageSource> {
        val cacheData = queryGalleryImageCache(gid, token, index)
        if (cacheData.isEmpty()) return PageInfo.emtpy()
        return PageInfo(
            cacheData.map { ImageSource(it) },
            index,
            0,
            0,
            cacheData.first().prevKey,
            cacheData.first().nextKey
        )
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGalleryInfo(info: GalleryInfoCache)

    @Query("SELECT * FROM ${DBValue.TABLE_GALLERY_INFO} WHERE ${DBValue.TABLE_GALLERY_INFO}.gid = :gid AND ${DBValue.TABLE_GALLERY_INFO}.token = :token LIMIT 1")
    abstract suspend fun queryGalleryInfo(gid: Long, token: String): GalleryInfoCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGalleryTags(tags: List<GalleryTagCache>)

    @Query("SELECT * FROM ${DBValue.TABLE_GALLERY_TAG} WHERE ${DBValue.TABLE_GALLERY_TAG}.gid = :gid AND ${DBValue.TABLE_GALLERY_TAG}.token = :token")
    abstract suspend fun queryGalleryTags(gid: Long, token: String): List<GalleryTagCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGalleryComments(comments: List<GalleryCommentCache>)

    @Query("SELECT * FROM ${DBValue.TABLE_GALLERY_COMMENT} WHERE ${DBValue.TABLE_GALLERY_COMMENT}.gid = :gid AND ${DBValue.TABLE_GALLERY_COMMENT}.token = :token")
    abstract suspend fun queryGalleryComments(gid: Long, token: String): List<GalleryCommentCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGalleryImageCache(sources: List<GalleryImageSourceCache>)

    @Query("SELECT * FROM ${DBValue.TABLE_IMAGE_SOURCE} WHERE ${DBValue.TABLE_IMAGE_SOURCE}.gid = :gid AND ${DBValue.TABLE_IMAGE_SOURCE}.token = :token AND ${DBValue.TABLE_IMAGE_SOURCE}.page = :page AND ${DBValue.TABLE_IMAGE_SOURCE}.timestamp > :timestamp")
    abstract suspend fun queryGalleryImageCache(
        gid: Long,
        token: String,
        page: Int,
        timestamp: Long = System.currentTimeMillis() - DBValue.IMAGE_SROUCE_CACHE_DURATION
    ): List<GalleryImageSourceCache>

    @Query("SELECT * FROM ${DBValue.TABLE_IMAGE_SOURCE} WHERE ${DBValue.TABLE_IMAGE_SOURCE}.gid = :gid AND ${DBValue.TABLE_IMAGE_SOURCE}.token = :token AND ${DBValue.TABLE_IMAGE_SOURCE}.`index` = :index AND ${DBValue.TABLE_IMAGE_SOURCE}.timestamp > :timestamp LIMIT 1")
    abstract suspend fun querySingleGalleryImageCache(
        gid: Long,
        token: String,
        index: Int,
        timestamp: Long = System.currentTimeMillis() - DBValue.IMAGE_SROUCE_CACHE_DURATION
    ): GalleryImageSourceCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGalleryPreview(vararg data: GalleryPreviewCache)

    @Query("SELECT * FROM ${DBValue.TABLE_GALLERY_PREVIEW} WHERE ${DBValue.TABLE_GALLERY_PREVIEW}.gid = :gid AND ${DBValue.TABLE_GALLERY_PREVIEW}.token = :token AND ${DBValue.TABLE_GALLERY_PREVIEW}.`index` = :index AND ${DBValue.TABLE_GALLERY_PREVIEW}.timestamp > :timestamp LIMIT 1")
    abstract suspend fun queryGalleryPreview(
        gid: Long,
        token: String,
        index: Int,
        timestamp: Long = System.currentTimeMillis() - DBValue.GALLERY_PREVIEW_CACHE_DURATION
    ): GalleryPreviewCache?
}