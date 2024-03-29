package com.mitsuki.ehit.model.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mitsuki.ehit.const.DBValue

@Entity(tableName = DBValue.TABLE_SEARCH_HISTORY)
data class SearchHistory(
    @PrimaryKey
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        return other is SearchHistory &&
                text == other.text
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}