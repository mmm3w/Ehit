package com.mitsuki.ehit.ui.gallerydetail.adapter

import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.model.entity.HeaderInfo
import com.mitsuki.ehit.ui.widget.CategoryView
import io.reactivex.rxjava3.subjects.PublishSubject

class GalleryDetailHeader(private val info: HeaderInfo) : SingleItemAdapter(true) {

    override val layoutRes: Int = R.layout.item_gallery_detail_header

    private val mSubject: PublishSubject<Event> by lazy { PublishSubject.create() }
    val event get() = mSubject.hideWithMainThread()

    private var mThumb: ImageView? = null
    private var mTitle: TextView? = null
    private var mUploader: TextView? = null
    private var mCategory: CategoryView? = null

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        mThumb = view(R.id.gallery_detail_thumb)
        mTitle = view(R.id.gallery_detail_title)
        mUploader = view<TextView>(R.id.gallery_detail_uploader)?.apply {
            setOnClickListener { mSubject.onNext(Event.Uploader) }
        }
        mCategory = view<CategoryView>(R.id.gallery_detail_category)?.apply {
            setOnClickListener { mSubject.onNext(Event.Category) }
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        with(info) {
            mThumb?.load(thumb) {
                allowHardware(false)
                placeholderMemoryCacheKey(cacheKey)
            }
            mTitle?.text = title
            mUploader?.text = uploader
            mCategory?.text = category
            mCategory?.setCategoryColor(categoryColor)
        }
    }


    sealed class Event {
        object Uploader : Event()
        object Category : Event()
    }
}