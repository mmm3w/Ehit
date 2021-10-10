package com.mitsuki.ehit.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.corners
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryBinding
import com.mitsuki.ehit.model.entity.Gallery
import java.util.*

class GalleryAdapter :
    PagingDataAdapter<Gallery, GalleryAdapter.ViewHolder>(Gallery.DIFF_CALLBACK), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        getItem(position)?.apply {
            post("click", GalleryClick(view, this))
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
            binding.galleryRating.isEnabled = false
        }

        fun bind(data: Gallery) {
            with(data) {
                binding.galleryThumb.load(thumb) { memoryCacheKey(CacheKey.thumbKey(gid, token)) }
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


    data class GalleryClick(val target: View, val data: Gallery)

}