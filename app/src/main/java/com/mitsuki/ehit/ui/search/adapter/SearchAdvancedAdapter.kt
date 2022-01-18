package com.mitsuki.ehit.ui.search.adapter

import com.mitsuki.armory.adapter.SingleItemBindingAdapter
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

    private var isGalleryNameChecked = true
    private var isGalleryTagsChecked = true
    private var isGalleryDescriptionChecked = false
    private var isTorrentFilenamesChecked = false
    private var isOnlyShowGalleriesWithTorrentsChecked = false
    private var isLowPowerTagsChecked = false
    private var isDownvotedTagsChecked = false
    private var isShowExpungedGalleriesChecked = false

    private var isDisableLanguageFilterChecked = false
    private var isDisableUploaderFilterChecked = false
    private var isDisableTagsFilterChecked = false

    private var mMinimumRating: Int? = null
    private var mBetweenPages: Pair<Int, Int>? = null

    override val onViewHolderCreate: ViewHolder<ItemSearchAdvancedBinding>.() -> Unit = {
        binding.searchAdvancedGalleryName.setOnCheckedChangeListener { _, isChecked ->
            isGalleryNameChecked = isChecked
        }
        binding.searchAdvancedGalleryTags.setOnCheckedChangeListener { _, isChecked ->
            isGalleryTagsChecked = isChecked
        }
        binding.searchAdvancedGalleryDescription.setOnCheckedChangeListener { _, isChecked ->
            isGalleryDescriptionChecked = isChecked
        }
        binding.searchAdvancedTorrentFilenames.setOnCheckedChangeListener { _, isChecked ->
            isTorrentFilenamesChecked = isChecked
        }
        binding.searchAdvancedOnlyShowGalleriesWithTorrents.setOnCheckedChangeListener { _, isChecked ->
            isOnlyShowGalleriesWithTorrentsChecked = isChecked
        }
        binding.searchAdvancedLowPowerTags.setOnCheckedChangeListener { _, isChecked ->
            isLowPowerTagsChecked = isChecked
        }
        binding.searchAdvancedDownvotedTags.setOnCheckedChangeListener { _, isChecked ->
            isDownvotedTagsChecked = isChecked
        }
        binding.searchAdvancedShowExpungedGalleries.setOnCheckedChangeListener { _, isChecked ->
            isShowExpungedGalleriesChecked = isChecked
        }

        binding.searchAdvancedDisableFilterLanguage.setOnCheckedChangeListener { _, isChecked ->
            isDisableLanguageFilterChecked = isChecked
        }
        binding.searchAdvancedDisableFilterUploader.setOnCheckedChangeListener { _, isChecked ->
            isDisableUploaderFilterChecked = isChecked
        }
        binding.searchAdvancedDisableFilterTags.setOnCheckedChangeListener { _, isChecked ->
            isDisableTagsFilterChecked = isChecked
        }


        binding.searchAdvancedMinimumRating.setOnCheckedChangeListener { _, isChecked ->
            mMinimumRating =
                if (isChecked) binding.searchAdvancedMinimumRatingSelect.hint.toString()
                    .toIntOrNull() else null
        }

        binding.searchAdvancedBetweenPage.setOnCheckedChangeListener { _, isChecked ->
            mBetweenPages =
                if (isChecked) {
                    val start = binding.searchAdvancedStartPage.text.toString().toIntOrNull() ?: -1
                    val end = binding.searchAdvancedEndPage.text.toString().toIntOrNull() ?: -1
                    if (start <= end && !(start == -1 && end == -1)) start to end else null
                } else null
        }

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
        with(searchKey) {
            isSearchGalleryName = isGalleryNameChecked
            isSearchGalleryTags = isGalleryTagsChecked
            isSearchGalleryDescription = isGalleryDescriptionChecked
            isSearchTorrentFilenames = isTorrentFilenamesChecked
            isOnlyShowGalleriesWithTorrents = isOnlyShowGalleriesWithTorrentsChecked
            isSearchLowPowerTags = isLowPowerTagsChecked
            isSearchDownvotedTags = isDownvotedTagsChecked
            isShowExpungedGalleries = isShowExpungedGalleriesChecked
            minimumRating = mMinimumRating
            betweenPages = mBetweenPages

            isDisableLanguageFilter = isDisableLanguageFilterChecked
            isDisableUploaderFilter = isDisableUploaderFilterChecked
            isDisableTagsFilter = isDisableTagsFilterChecked
        }
    }

    fun submitData(searchKey: SearchKey) {
        tempKey = searchKey
        if (isEnable) notifyItemChanged(0)
    }

    fun applyRating(data: Pair<Int, Int>) {
        tempKey?.minimumRating = data.first
        if (isEnable) notifyItemChanged(0)
    }
}