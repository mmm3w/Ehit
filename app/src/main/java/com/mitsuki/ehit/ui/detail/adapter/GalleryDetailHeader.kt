package com.mitsuki.ehit.ui.detail.adapter

import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.model.entity.HeaderInfo
import com.mitsuki.ehit.ui.common.widget.CategoryView
import io.reactivex.rxjava3.subjects.PublishSubject

class GalleryDetailHeader(private val info: HeaderInfo) : SingleItemAdapter(true), EventEmitter {

    companion object {
        const val UPLOADER = "Uploader"
        const val CATEGORY = "Category"
    }

    override val eventEmitter: Emitter = Emitter()

    override val layoutRes: Int = R.layout.item_gallery_detail_header

    private var mThumb: ImageView? = null
    private var mTitle: TextView? = null
    private var mUploader: TextView? = null
    private var mCategory: CategoryView? = null

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        mThumb = view(R.id.gallery_detail_thumb)
        mTitle = view(R.id.gallery_detail_title)
        mUploader = view<TextView>(R.id.gallery_detail_uploader)?.apply {
            setOnClickListener { post("header", UPLOADER) }
        }
        mCategory = view<CategoryView>(R.id.gallery_detail_category)?.apply {
            setOnClickListener { post("header", CATEGORY) }
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        with(info) {
            mThumb?.load(thumb) { placeholderMemoryCacheKey(cacheKey) }
            mTitle?.text = title
            mUploader?.text = uploader
            mCategory?.text = category
            mCategory?.setCategoryColor(categoryColor)
        }
    }


}