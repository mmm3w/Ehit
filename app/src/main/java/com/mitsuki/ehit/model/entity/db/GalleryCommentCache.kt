package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.Comment

@Entity(
    tableName = DBValue.TABLE_GALLERY_COMMENT,
    primaryKeys = ["cid"],
    foreignKeys = [ForeignKey(
        entity = GalleryInfoCache::class,
        parentColumns = ["gid", "token"],
        childColumns = ["gid", "token"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["gid", "token"])],
)
class GalleryCommentCache(
    @ColumnInfo(name = "gid") val gid: Long,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "cid") val cid: Long,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "post_time") val postTime: Long,
    @ColumnInfo(name = "last_edited_time") val lastEditedTime: Long,
    @ColumnInfo(name = "score") val score: String,
    @ColumnInfo(name = "editable") val editable: Boolean,
    @ColumnInfo(name = "vote_enable") val voteEnable: Boolean,
    @ColumnInfo(name = "vote_state") val voteState: Int,
    @ColumnInfo(name = "vote_info") val voteInfo: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)