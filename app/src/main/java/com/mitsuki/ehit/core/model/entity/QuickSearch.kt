package com.mitsuki.ehit.core.model.entity

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_search")
data class QuickSearch(
    @PrimaryKey val text: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<QuickSearch>() {
            override fun areItemsTheSame(oldItem: QuickSearch, newItem: QuickSearch): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: QuickSearch,
                newItem: QuickSearch
            ): Boolean {
                return oldItem.text == newItem.text
            }

        }
    }
}