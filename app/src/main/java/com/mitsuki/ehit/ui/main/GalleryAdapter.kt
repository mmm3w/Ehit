package com.mitsuki.ehit.ui.main

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.memory.MemoryCache
import coil.metadata
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.crutch.extend.corners
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryBinding
import com.mitsuki.ehit.model.entity.Gallery
import java.util.*

class GalleryAdapter :
    PagingDataAdapter<Gallery, GalleryAdapter.ViewHolder>(Gallery.DIFF_CALLBACK) {

    val clickEvent: SingleLiveEvent<GalleryClick> by lazy { SingleLiveEvent() }

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        val imageView = view.findViewById<ImageView>(R.id.gallery_thumb)
        getItem(position)?.apply {
            clickEvent.postValue(GalleryClick(view, imageView.metadata?.memoryCacheKey, this))
        }
        Unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery)) {
        val binding by viewBinding(ItemGalleryBinding::bind)

        init {
            binding.galleryThumb.corners(dp2px(4f))
        }

        fun bind(data: Gallery) {
            with(data) {
                binding.galleryThumb.load(thumb)
                binding.galleryTitle.text = title
                binding.galleryUploader.text = uploader
                binding.galleryLang.text = languageSimple
                binding.galleryCategory.apply {
                    setCategoryColor(categoryColor)
                    text = category.toUpperCase(Locale.getDefault())
                }
                binding.galleryTime.text = time
                binding.galleryRating.rating = rating

                ViewCompat.setTransitionName(itemView, itemTransitionName)
            }
        }

    }


    data class GalleryClick(val target: View, val cacheKey: MemoryCache.Key?, val data: Gallery)
}