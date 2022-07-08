package com.mitsuki.ehit.ui.main.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import coil.load
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.extensions.corners
import com.mitsuki.ehit.databinding.ItemGalleryBinding
import com.mitsuki.ehit.model.ehparser.Category
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder

class GalleryItemViewHolder(parent: ViewGroup) : BindingViewHolder<ItemGalleryBinding>(
    parent,
    R.layout.item_gallery,
    ItemGalleryBinding::bind
) {
    init {
        binding.galleryThumb.corners(dp2px(4f))
        binding.galleryRating.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    fun bind(
        gid: Long,
        token: String,
        thumb: String,
        title: String,
        uploader: String,
        languageSimple: String,
        category: String,
        time: String,
        rating: Float,
        page: Int,
        transitionName: String
    ) {
        binding.galleryThumb.load(thumb) {
            memoryCacheKey(
                CacheKey.thumbKey(
                    gid,
                    token
                )
            )
        }
        binding.galleryTitle.text = title
        binding.galleryUploader.text = uploader
        binding.galleryLang.apply {
            isVisible = languageSimple.isNotEmpty()
            text = languageSimple
        }
        binding.galleryCategory.apply {
            setCategoryColor(Category.getColor(category))
            text = category.uppercase()
        }
        binding.galleryTime.text = time
        binding.galleryRating.rating = rating
        binding.galleryPage.apply {
            isVisible = page > 0
            text = "${page}P"
        }
        ViewCompat.setTransitionName(itemView, transitionName)
    }

}