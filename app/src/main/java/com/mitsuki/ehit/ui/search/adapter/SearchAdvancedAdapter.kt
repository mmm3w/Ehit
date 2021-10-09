package com.mitsuki.ehit.ui.search.adapter

import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.adapter.SingleItemBindingAdapter
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemSearchAdvancedBinding
import com.mitsuki.ehit.model.ehparser.GalleryRating
import com.mitsuki.ehit.model.entity.SearchKey

class SearchAdvancedAdapter :
    SingleItemBindingAdapter<ItemSearchAdvancedBinding>(
        R.layout.item_search_advanced,
        ItemSearchAdvancedBinding::bind
    ), EventEmitter {

    var isVisible: Boolean = false
        set(value) {
            if (value != field) {
                if (value && !field && isEnable) {
                    notifyItemInserted(0)
                } else if (!value && field && isEnable) {
                    notifyItemRemoved(0)
                }
                field = value
            }
        }

    override var isEnable: Boolean = false
        set(value) {
            if (value != field) {
                if (value && !field && isVisible) {
                    notifyItemInserted(0)
                } else if (!value && field && isVisible) {
                    notifyItemRemoved(0)
                }
                field = value
            }
        }

    override val eventEmitter: Emitter = Emitter()

    override fun getItemCount(): Int = if (isVisible && isEnable) 1 else 0

    private var tempKey: SearchKey? = null

    override val onViewHolderCreate: ViewHolder<ItemSearchAdvancedBinding>.() -> Unit = {
        binding.searchAdvancedMinimumRatingSelect.apply {
            hint = GalleryRating.DATA[0].first.toString()
            setText(GalleryRating.DATA[0].second)
            setOnClickListener { post("rating", 0) }
        }
    }

    override val onViewHolderBind: ViewHolder<ItemSearchAdvancedBinding>.() -> Unit = {
        tempKey?.apply {
            binding.searchAdvancedGalleryName.isChecked = isSearchGalleryName
            binding.searchAdvancedGalleryTags.isChecked = isSearchGalleryTags
            binding.searchAdvancedGalleryDescription.isChecked = isSearchGalleryDescription
            binding.searchAdvancedTorrentFilenames.isChecked = isSearchTorrentFilenames
            binding.searchAdvancedOnlyShowGalleriesWithTorrents.isChecked =
                isOnlyShowGalleriesWithTorrents
            binding.searchAdvancedLowPowerTags.isChecked = isSearchLowPowerTags
            binding.searchAdvancedDownvotedTags.isChecked = isSearchDownvotedTags
            binding.searchAdvancedShowExpungedGalleries.isChecked = isShowExpungedGalleries

            binding.searchAdvancedMinimumRating.isChecked = minimumRating?.apply {
                binding.searchAdvancedMinimumRatingSelect.hint = toString()
                binding.searchAdvancedMinimumRatingSelect.setText(GalleryRating.findText(this))
            } != null

            binding.searchAdvancedBetweenPage.isChecked = betweenPages?.apply {
                binding.searchAdvancedStartPage.setText(first.toString())
                binding.searchAdvancedEndPage.setText(second.toString())
            } != null

            binding.searchAdvancedDisableFilterLanguage.isChecked = isDisableLanguageFilter
            binding.searchAdvancedDisableFilterUploader.isChecked = isDisableUploaderFilter
            binding.searchAdvancedDisableFilterTags.isChecked = isDisableTagsFilter
        }
    }


    fun getOptions(searchKey: SearchKey) {
        //TODO 修改为监听每个view来存储数据，这里再最后获取


//        with(searchKey) {
//            isSearchGalleryName = galleryName.checkState()
//            isSearchGalleryTags = galleryTags.checkState()
//            isSearchGalleryDescription = galleryDescription.checkState()
//            isSearchTorrentFilenames = torrentFilenames.checkState()
//            isOnlyShowGalleriesWithTorrents = onlyShowGalleriesWithTorrents.checkState()
//            isSearchLowPowerTags = lowPowerTags.checkState()
//            isSearchDownvotedTags = downvotedTags.checkState()
//            isShowExpungedGalleries = showExpungedGalleries.checkState()
//            minimumRating =
//                if (minimumRatingCheck.checkState())
//                    minimumRatingSelect?.hint.toString().toIntOrNull()
//                else null
//
//            betweenPages = if (betweenPage.checkState()) {
//                val start = startPage?.text.toString().toIntOrNull()
//                val end = endPage?.text.toString().toIntOrNull()
//                if (start != null && end != null && start <= end) Pair(start, end) else null
//            } else null
//
//            isDisableLanguageFilter = disableFilterLanguage.checkState()
//            isDisableUploaderFilter = disableFilterUploader.checkState()
//            isDisableTagsFilter = disableFilterTags.checkState()
//        }
    }

    fun submitData(searchKey: SearchKey) {
        tempKey = searchKey
        if (isEnable) notifyItemChanged(0)
    }

    fun applyRating(data: Pair<Int, Int>) {
        //TODO 这里也不应该这样直接进行修改，而是先修改源数据再更新列表来实现数据更新显示 虽说表现上是View，但是行为操作还是需要依照适配器的逻辑来

//        minimumRatingSelect?.apply {
//            hint = data.first.toString()
//            setText(data.second)
//        }
    }

    private fun CheckBox?.checkState(): Boolean {
        return this?.isChecked ?: false
    }


}