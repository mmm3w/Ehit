package com.mitsuki.ehit.mvvm.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.model.entity.*
import com.mitsuki.ehit.mvvm.ui.widget.CategoryView
import java.util.*

class GalleryDetailAdapter :
    PagingDataAdapter<GalleryDetailItem, RecyclerView.ViewHolder>(GalleryDetailWrap.DIFF_CALLBACK) {


    val mSpanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (getItemViewType(position) == GALLERY_DETAIL_PREVIEW) return 1
                return 3
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return obtainViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            GALLERY_DETAIL_HEADER ->
                (holder as DetailHeaderViewHolder).run {
                    binds(this, position, getItem(position) as DetailHeader)
                }
            GALLERY_DETAIL_PART ->
                (holder as DetailPartViewHolder).run {
                    binds(this, position, getItem(position) as DetailPart)
                }
            GALLERY_DETAIL_OPERATING ->
                (holder as DetailOperatingViewHolder).run {
                    binds(this, position, getItem(position) as DetailOperating)
                }
            GALLERY_DETAIL_TAG ->
                (holder as DetailTagViewHolder).run {
                    binds(this, position, getItem(position) as DetailTag)
                }
            GALLERY_DETAIL_COMMENT ->
                (holder as DetailCommentViewHolder).run {
                    binds(this, position, getItem(position) as DetailComment)
                }
            GALLERY_DETAIL_PREVIEW ->
                (holder as DetailPreviewViewHolder).run {
                    binds(this, position, getItem(position) as DetailPreview)
                }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is DetailHeader)
            return GALLERY_DETAIL_HEADER
        if (getItem(position) is DetailPart)
            return GALLERY_DETAIL_PART
        if (getItem(position) is DetailOperating)
            return GALLERY_DETAIL_OPERATING
        if (getItem(position) is DetailTag)
            return GALLERY_DETAIL_TAG
        if (getItem(position) is DetailComment)
            return GALLERY_DETAIL_COMMENT
        if (getItem(position) is DetailPreview)
            return GALLERY_DETAIL_PREVIEW
        return GALLERY_DETAIL_EMPTY
    }

    private fun obtainViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GALLERY_DETAIL_HEADER ->
                DetailHeaderViewHolder(createView(parent, R.layout.item_gallery_detail_header))
            GALLERY_DETAIL_PART ->
                DetailPartViewHolder(createView(parent, R.layout.item_gallery_detail_part))
            GALLERY_DETAIL_OPERATING ->
                DetailOperatingViewHolder(createView(parent, R.layout.item_gallery_detail_op))
            GALLERY_DETAIL_TAG ->
                DetailTagViewHolder(createView(parent, R.layout.item_gallery_detail_tag))
            GALLERY_DETAIL_COMMENT ->
                DetailCommentViewHolder(createView(parent, R.layout.item_gallery_detail_comment))
            GALLERY_DETAIL_PREVIEW ->
                DetailPreviewViewHolder(createView(parent, R.layout.item_gallery_detail_preview))
            else ->
                EmptyViewHolder(View(parent.context))
        }
    }

    private fun createView(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    companion object {
        const val GALLERY_DETAIL_EMPTY = -1
        const val GALLERY_DETAIL_HEADER = 0
        const val GALLERY_DETAIL_PART = 1
        const val GALLERY_DETAIL_OPERATING = 2
        const val GALLERY_DETAIL_TAG = 3
        const val GALLERY_DETAIL_COMMENT = 4
        const val GALLERY_DETAIL_PREVIEW = 5
    }
}

