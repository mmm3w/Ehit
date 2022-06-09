package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue
import com.mitsuki.ehit.model.entity.GalleryDataMeta

@Entity(
    tableName = DBValue.TABLE_QUICK_SEARCH,
    indices = [Index(value = ["type", "keyword"], unique = true)]
)
data class QuickSearch(
    @ColumnInfo(name = "type") val type: GalleryDataMeta.Type,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "keyword") val key: String,
    @ColumnInfo(name = "sort") var sort: Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") val _id: Long = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return other is QuickSearch &&
                type == other.type &&
                name == other.name &&
                key == other.key &&
                sort == other.sort
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + sort
        return result
    }

}