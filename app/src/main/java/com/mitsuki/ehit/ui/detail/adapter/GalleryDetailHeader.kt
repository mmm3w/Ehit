package com.mitsuki.ehit.ui.detail.adapter

import androidx.core.view.isVisible
import coil.load
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemGalleryDetailHeaderBinding
import com.mitsuki.ehit.model.entity.GalleryDetailWrap

class GalleryDetailHeader(private val mData: GalleryDetailWrap) :
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
        mData.headerInfo.also { info ->
            with(binding) {
                galleryDetailThumb.apply {
                    if (info.thumb.isNotEmpty())
                        load(info.thumb) { placeholderMemoryCacheKey(info.cacheKey) }
                    isVisible = info.thumb.isNotEmpty()
                }
                galleryDetailTitle.apply {
                    isVisible = info.title.isNotEmpty()
                    text = info.title
                }
                galleryDetailUploader.apply {
                    isVisible = info.uploader.isNotEmpty()
                    text = info.uploader
                }
                galleryDetailCategory.apply {
                    isVisible = info.category.isNotEmpty()
                    text = info.category
                    setCategoryColor(info.categoryColor)
                }
            }
        }
    }

    fun tryRefresh() {
        if (mData.isHeaderUpdate) {
            notifyItemChanged(0)
            mData.isHeaderUpdate = false
        }
    }
}