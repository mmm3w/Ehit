package com.mitsuki.ehit.ui.adapter

import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.model.ehparser.GalleryRating
import com.mitsuki.ehit.model.entity.SearchKey

class SearchAdvancedAdapter : SingleItemAdapter(true) {

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

    val ratingEvent: MutableLiveData<String> = MutableLiveData()

    override fun getItemCount(): Int = if (isVisible && isEnable) 1 else 0

    override val layoutRes: Int = R.layout.item_search_advanced

    private var galleryName: CheckBox? = null
    private var galleryTags: CheckBox? = null
    private var galleryDescription: CheckBox? = null
    private var torrentFilenames: CheckBox? = null
    private var onlyShowGalleriesWithTorrents: CheckBox? = null
    private var lowPowerTags: CheckBox? = null
    private var downvotedTags: CheckBox? = null
    private var showExpungedGalleries: CheckBox? = null
    private var minimumRatingCheck: CheckBox? = null
    private var minimumRatingSelect: TextView? = null
    private var betweenPage: CheckBox? = null
    private var startPage: EditText? = null
    private var endPage: EditText? = null
    private var disableFilterLanguage: CheckBox? = null
    private var disableFilterUploader: CheckBox? = null
    private var disableFilterTags: CheckBox? = null

    private var tempKey: SearchKey? = null

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        galleryName = view(R.id.search_advanced_gallery_name)
        galleryTags = view(R.id.search_advanced_gallery_tags)
        galleryDescription = view(R.id.search_advanced_gallery_description)
        torrentFilenames = view(R.id.search_advanced_torrent_filenames)
        onlyShowGalleriesWithTorrents = view(R.id.search_advanced_only_show_galleries_with_torrents)
        lowPowerTags = view(R.id.search_advanced_low_power_tags)
        downvotedTags = view(R.id.search_advanced_downvoted_tags)
        showExpungedGalleries = view(R.id.search_advanced_show_expunged_galleries)
        minimumRatingCheck = view(R.id.search_advanced_minimum_rating)
        minimumRatingSelect = view<TextView>(R.id.search_advanced_minimum_rating_select)?.apply {
            hint = GalleryRating.DATA[0].first.toString()
            setText(GalleryRating.DATA[0].second)
            setOnClickListener { ratingEvent.postValue("") }
        }
        betweenPage = view(R.id.search_advanced_between_page)
        startPage = view(R.id.search_advanced_start_page)
        endPage = view(R.id.search_advanced_end_page)
        disableFilterLanguage = view(R.id.search_advanced_disable_filter_language)
        disableFilterUploader = view(R.id.search_advanced_disable_filter_uploader)
        disableFilterTags = view(R.id.search_advanced_disable_filter_tags)
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        tempKey?.apply {
            galleryName?.isChecked = isSearchGalleryName
            galleryTags?.isChecked = isSearchGalleryTags
            galleryDescription?.isChecked = isSearchGalleryDescription
            torrentFilenames?.isChecked = isSearchTorrentFilenames
            onlyShowGalleriesWithTorrents?.isChecked = isOnlyShowGalleriesWithTorrents
            lowPowerTags?.isChecked = isSearchLowPowerTags
            downvotedTags?.isChecked = isSearchDownvotedTags
            showExpungedGalleries?.isChecked = isShowExpungedGalleries

            minimumRatingCheck?.isChecked = minimumRating?.apply {
                minimumRatingSelect?.hint = toString()
                minimumRatingSelect?.setText(GalleryRating.findText(this))
            } != null

            betweenPage?.isChecked = betweenPages?.apply {
                startPage?.setText(first.toString())
                endPage?.setText(second.toString())
            } != null

            disableFilterLanguage?.isChecked = isDisableLanguageFilter
            disableFilterUploader?.isChecked = isDisableUploaderFilter
            disableFilterTags?.isChecked = isDisableTagsFilter
        }
    }


    fun getOptions(searchKey: SearchKey) {
        with(searchKey) {
            isSearchGalleryName = galleryName.checkState()
            isSearchGalleryTags = galleryTags.checkState()
            isSearchGalleryDescription = galleryDescription.checkState()
            isSearchTorrentFilenames = torrentFilenames.checkState()
            isOnlyShowGalleriesWithTorrents = onlyShowGalleriesWithTorrents.checkState()
            isSearchLowPowerTags = lowPowerTags.checkState()
            isSearchDownvotedTags = downvotedTags.checkState()
            isShowExpungedGalleries = showExpungedGalleries.checkState()
            minimumRating =
                if (minimumRatingCheck.checkState())
                    minimumRatingSelect?.hint.toString().toIntOrNull()
                else null

            betweenPages = if (betweenPage.checkState()) {
                val start = startPage?.text.toString().toIntOrNull()
                val end = endPage?.text.toString().toIntOrNull()
                if (start != null && end != null && start <= end) Pair(start, end) else null
            } else null

            isDisableLanguageFilter = disableFilterLanguage.checkState()
            isDisableUploaderFilter = disableFilterUploader.checkState()
            isDisableTagsFilter = disableFilterTags.checkState()
        }
    }

    fun submitData(searchKey: SearchKey) {
        tempKey = searchKey
        if (isEnable) notifyItemChanged(0)
    }

    fun applyRating(data: Pair<Int, Int>) {
        minimumRatingSelect?.apply {
            hint = data.first.toString()
            setText(data.second)
        }
    }

    private fun CheckBox?.checkState(): Boolean {
        return this?.isChecked ?: false
    }

}