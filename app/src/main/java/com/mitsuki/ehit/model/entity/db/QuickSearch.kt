package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.page.GalleryListPageIn

@Entity(
    tableName = DBValue.TABLE_QUICK_SEARCH,
    indices = [Index(value = ["type", "key"], unique = true)]
)
data class QuickSearch(
    @ColumnInfo(name = "type") val type: GalleryListPageIn.Type,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "sort") val sort: Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
}