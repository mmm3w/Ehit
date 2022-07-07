package com.mitsuki.ehit.ui.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.corners
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemGalleryBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder
import java.util.*

class GalleryListAdapter :
    PagingDataAdapter<Gallery, GalleryListAdapter.ViewHolder>(Diff.GALLERY_DIFF_CALLBACK),
    EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    var isPageShow = false

    private val mItemClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        peek(position)?.apply {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            with(it) {
                holder.binding.galleryThumb.load(thumb) {
                    memoryCacheKey(
                        CacheKey.thumbKey(
                            gid,
                            token
                        )
                    )
                }
                holder.binding.galleryTitle.text = title
                holder.binding.galleryUploader.text = uploader
                holder.binding.galleryLang.apply {
                    isVisible = languageSimple.isNotEmpty()
                    text = languageSimple
                }
                holder.binding.galleryCategory.apply {
                    setCategoryColor(categoryColor)
                    text = category.uppercase()
                }
                holder.binding.galleryTime.text = time
                holder.binding.galleryRating.rating = rating
                holder.binding.galleryPage.apply {
                    isVisible = isPageShow && page > 0
                    text = "${page}P"
                }
                ViewCompat.setTransitionName(holder.itemView, itemTransitionName)
            }
        }
    }

    class ViewHolder(parent: ViewGroup) : BindingViewHolder<ItemGalleryBinding>(
        parent,
        R.layout.item_gallery,
        ItemGalleryBinding::bind
    ) {
        init {
            binding.galleryThumb.corners(dp2px(4f))
            binding.galleryRating.isEnabled = false
        }
    }

    data class GalleryClick(val target: View, val data: Gallery)
}