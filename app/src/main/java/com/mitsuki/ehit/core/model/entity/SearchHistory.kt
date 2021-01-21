package com.mitsuki.ehit.core.model.entity

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey val text: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SearchHistory>() {
            override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SearchHistory,
                newItem: SearchHistory
            ): Boolean {
                return oldItem.text == newItem.text &&
                        oldItem.createdAt == newItem.createdAt
            }

        }
    }
}