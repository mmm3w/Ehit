package com.mitsuki.ehit.ui.detail.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemGalleryDetailOperatingBinding
import com.mitsuki.ehit.model.entity.DetailPart
import com.mitsuki.ehit.ui.detail.layoutmanager.DetailOperatingLayoutManager

class GalleryDetailOperatingBlock :
    SingleItemBindingAdapter<ItemGalleryDetailOperatingBinding>(
        R.layout.item_gallery_detail_operating,
        ItemGalleryDetailOperatingBinding::bind,
        false
    ), EventEmitter {

    companion object {
        const val SCORE = "Score"
        const val SIMILARITYSEARCH = "SimilaritySearch"
        const val MOREINFO = "MoreInfo"
        const val DOWNLOAD = "Download"
        const val READ = "Read"
    }

    override val eventEmitter: Emitter = Emitter()

    var data: DetailPart? = null
        set(value) {
            if (value != field) {
                when {
                    isEnable && value == null -> isEnable = false
                    !isEnable && value != null -> isEnable = true
                    isEnable && value != null -> notifyItemChanged(0)
                }
                field = value
            }
        }

    private val mOperatingAdapter by lazy { GalleryDetailOperatingPart(this) }

    override val onViewHolderCreate: ViewHolder<ItemGalleryDetailOperatingBinding>.() -> Unit = {
        binding.galleryDetailDownload.setOnClickListener { post("operating", DOWNLOAD) }
        binding.galleryDetailRead.setOnClickListener { post("operating", READ) }
        binding.galleryDetailPart.apply {
            layoutManager = DetailOperatingLayoutManager(context)
            adapter = mOperatingAdapter
            addItemDecoration(mOperatingAdapter.divider)
        }
    }

    override val onViewHolderBind: ViewHolder<ItemGalleryDetailOperatingBinding>.() -> Unit = {
        mOperatingAdapter.data = data
    }
}