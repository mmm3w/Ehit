package com.mitsuki.ehit.ui.main.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.Gallery

class GalleryListAdapter :
    PagingDataAdapter<Gallery, GalleryItemViewHolder>(Diff.GALLERY_DIFF_CALLBACK),
    EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    var isPageShow = false

    private val mItemClick = { view: View ->
        val position = (view.tag as GalleryItemViewHolder).bindingAdapterPosition
        peek(position)?.apply {
            post("click", GalleryClick(view, this))
        }
        Unit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        return GalleryItemViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        getItem(position)?.let {
            with(it) {
                holder.bind(
                    gid,
                    token,
                    thumb,
                    title,
                    uploader,
                    languageSimple,
                    category,
                    time,
                    rating,
                    if (isPageShow) page else 0,
                    itemTransitionName
                )
            }
        }
    }

}