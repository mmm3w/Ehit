package com.mitsuki.ehit.ui.detail.adapter

import coil.load
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemGalleryDetailHeaderBinding
import com.mitsuki.ehit.model.entity.HeaderInfo

class GalleryDetailHeader(private val info: HeaderInfo) :
    SingleItemBindingAdapter<ItemGalleryDetailHeaderBinding>(
        R.layout.item_gallery_detail_header, ItemGalleryDetailHeaderBinding::bind
    ), EventEmitter {

    companion object {
        const val UPLOADER = "Uploader"
        const val CATEGORY = "Category"
    }

    override val eventEmitter: Emitter = Emitter()

    override val onViewHolderCreate: ViewHolder<ItemGalleryDetailHeaderBinding>.() -> Unit = {
        binding.galleryDetailUploader.setOnClickListener { post("header", UPLOADER) }
        binding.galleryDetailCategory.setOnClickListener { post("header", CATEGORY) }
    }

    override val onViewHolderBind: ViewHolder<ItemGalleryDetailHeaderBinding>.() -> Unit = {
        with(binding) {
            galleryDetailThumb.load(info.thumb) { placeholderMemoryCacheKey(info.cacheKey) }
            galleryDetailTitle.text = info.title
            galleryDetailUploader.text = info.uploader
            galleryDetailCategory.text = info.category
            galleryDetailCategory.setCategoryColor(info.categoryColor)
        }
    }
}