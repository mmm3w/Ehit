package com.mitsuki.ehit.model.diff

import androidx.recyclerview.widget.DiffUtil
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.model.entity.db.DownloadBaseInfo
import com.mitsuki.ehit.model.entity.db.QuickSearch
import com.mitsuki.ehit.model.entity.db.SearchHistory

object Diff {
    val GALLERY_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Gallery>() {
        override fun areItemsTheSame(
            oldConcert: Gallery,
            newConcert: Gallery
        ): Boolean = true

        override fun areContentsTheSame(
            oldConcert: Gallery,
            newConcert: Gallery
        ): Boolean {
            return oldConcert.token == newConcert.token
                    && oldConcert.category == newConcert.category
                    && oldConcert.time == newConcert.time
                    && oldConcert.title == newConcert.title
                    && oldConcert.uploader == newConcert.uploader
                    && oldConcert.thumb == newConcert.thumb
                    && oldConcert.rating == newConcert.rating
                    && oldConcert.language == newConcert.language
        }
    }

    val SEARCH_HISTORY by lazy {
        object : DiffUtil.ItemCallback<SearchHistory>() {
            override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean =
                true

            override fun areContentsTheSame(
                oldItem: SearchHistory,
                newItem: SearchHistory
            ): Boolean = oldItem == newItem
        }
    }

    val QUICK_SEARCH by lazy {
        object : DiffUtil.ItemCallback<QuickSearch>() {
            override fun areItemsTheSame(oldItem: QuickSearch, newItem: QuickSearch): Boolean = true

            override fun areContentsTheSame(
                oldItem: QuickSearch,
                newItem: QuickSearch
            ): Boolean = oldItem == newItem
        }
    }

    val IMAGE_SOURCE by lazy {
        object : DiffUtil.ItemCallback<ImageSource>() {
            override fun areItemsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean = true

            override fun areContentsTheSame(
                oldConcert: ImageSource,
                newConcert: ImageSource
            ): Boolean = oldConcert == newConcert
        }
    }

    val GALLERY_FAVORITES by lazy {
        object : DiffUtil.ItemCallback<Pair<String, Int>>() {
            override fun areItemsTheSame(
                oldItem: Pair<String, Int>,
                newItem: Pair<String, Int>
            ): Boolean = true

            override fun areContentsTheSame(
                oldItem: Pair<String, Int>,
                newItem: Pair<String, Int>
            ): Boolean {
                return oldItem.first == newItem.first &&
                        oldItem.second == newItem.second
            }
        }
    }

    val GALLERY_COMMENT by lazy {
        object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                true

            override fun areContentsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean = oldItem == newItem
        }
    }

    val DOWNLOAD_LIST_INFO by lazy {
        object : DiffUtil.ItemCallback<DownloadListInfo>() {
            override fun areItemsTheSame(oldItem: DownloadListInfo, newItem: DownloadListInfo): Boolean =
                true

            override fun areContentsTheSame(
                oldItem: DownloadListInfo,
                newItem: DownloadListInfo
            ): Boolean = oldItem == newItem
        }
    }
}