package com.mitsuki.ehit.ui.adapter

import android.graphics.Outline
import android.media.Image
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import coil.metadata
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.view
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.ui.widget.CategoryView
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class GalleryAdapter :
    PagingDataAdapter<Gallery, GalleryAdapter.ViewHolder>(Gallery.DIFF_CALLBACK) {

    private val mSubject = PublishSubject.create<GalleryClick>()

    val clickEvent get() = mSubject.hideWithMainThread()

    private val mGalleryThumbViewOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, 8F)
        }
    }

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        val imageView = view.findViewById<ImageView>(R.id.gallery_thumb)
        getItem(position)?.apply {
            mSubject.onNext(GalleryClick(view, imageView.metadata?.memoryCacheKey, this))
        }
        Unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, mGalleryThumbViewOutlineProvider).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup, provider: ViewOutlineProvider) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery)) {

        private val mGalleryThumb = view<ImageView>(R.id.gallery_thumb)?.apply {
            outlineProvider = provider
            clipToOutline = true
        }

        private val mGalleryTitle = view<TextView>(R.id.gallery_title)
        private val mGalleryUploader = view<TextView>(R.id.gallery_uploader)
        private val mGalleryLanguage = view<TextView>(R.id.gallery_lang)
        private val mGalleryCategory = view<CategoryView>(R.id.gallery_category)
        private val mGalleryTime = view<TextView>(R.id.gallery_time)
        private val mGalleryRating =
            view<RatingView>(R.id.gallery_rating)?.apply {
                intervalPadding = dp2px(2f)
                isEnabled = false
            }
        private val mGalleryLayout = view<ConstraintLayout>(R.id.gallery_layout)

        fun bind(data: Gallery) {
            with(data) {
                mGalleryThumb?.load(thumb)
                mGalleryTitle?.text = title
                mGalleryUploader?.text = uploader
                mGalleryLanguage?.text = languageSimple
                mGalleryCategory?.apply {
                    setCategoryColor(categoryColor)
                    text = category.toUpperCase(Locale.getDefault())
                }
                mGalleryTime?.text = time
                mGalleryRating?.rating = rating
                ViewCompat.setTransitionName(itemView, itemTransitionName)
                mGalleryThumb?.metadata?.memoryCacheKey
            }
        }

    }


    data class GalleryClick(val target: View, val cacheKey: MemoryCache.Key?, val data: Gallery)
}