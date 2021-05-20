package com.mitsuki.ehit.model.dao

import androidx.room.*
import com.mitsuki.ehit.const.DBKey
import com.mitsuki.ehit.model.entity.GalleryDetail
import com.mitsuki.ehit.model.entity.TagGroup
import com.mitsuki.ehit.model.entity.db.GalleryInfoCache
import com.mitsuki.ehit.model.entity.db.GalleryTagCache

@Dao
abstract class GalleryDao {

    @Transaction
    fun insertGalleryDetail(data: GalleryDetail) {
        insertGalleryInfo(data.info)
        insertGalleryTags(data.tagCache)
    }

    @Transaction
    fun queryGallery(gid: Long, token: String): GalleryDetail? {
        val info = queryGalleryInfo(gid, token) ?: return null
        val tag = queryGalleryTags(gid, token).groupBy { it.group }.map { map ->
            TagGroup(map.key, map.value.map { it.name }.toTypedArray())
        }

        return GalleryDetail(info, tag)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGalleryInfo(info: GalleryInfoCache)

    @Query("SELECT * FROM ${DBKey.TABLE_GALLERY_INFO} WHERE ${DBKey.TABLE_GALLERY_INFO}.gid = :gid AND ${DBKey.TABLE_GALLERY_INFO}.token = :token LIMIT 1")
    abstract fun queryGalleryInfo(gid: Long, token: String): GalleryInfoCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGalleryTags(tags: List<GalleryTagCache>)

    @Query("SELECT * FROM ${DBKey.TABLE_GALLERY_TAG} WHERE ${DBKey.TABLE_GALLERY_TAG}.gid = :gid AND ${DBKey.TABLE_GALLERY_TAG}.token = :token")
    abstract fun queryGalleryTags(gid: Long, token: String): List<GalleryTagCache>

}