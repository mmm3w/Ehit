package com.mitsuki.ehit.ui.detail.adapter

import androidx.paging.LoadState
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemGalleryDetailOperatingBinding
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.ui.detail.layoutmanager.DetailOperatingLayoutManager

//详情adapter 02
class GalleryDetailOperatingBlock(private val mData: GalleryDetailWrap) :
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

    private val mOperatingAdapter by lazy { GalleryDetailOperatingPart(this) }
    private val mGate = InitialGate()

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (mGate.ignore()) return
            if (field != loadState) {
                when (loadState) {
                    is LoadState.Loading -> mGate.prep(true)
                    is LoadState.Error -> mGate.prep(false)
                    is LoadState.NotLoading -> mGate.trigger()
                }
                if (mGate.ignore()) isEnable = true
                field = loadState
            }
        }

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
        mOperatingAdapter.data = mData.partInfo
        mOperatingAdapter.notifyItemChanged(0)
    }
}