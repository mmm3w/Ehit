package com.mitsuki.ehit.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.utils.recordTime
import com.mitsuki.ehit.model.ehparser.Language
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.entity.db.GalleryInfoCache
import com.mitsuki.ehit.ui.main.adapter.GalleryClick
import com.mitsuki.ehit.ui.main.adapter.GalleryItemViewHolder

class HistoryAdapter(private val mData: NotifyQueueData<GalleryInfoCache>) :
    RecyclerView.Adapter<GalleryItemViewHolder>(), EventEmitter {

    init {
        mData.attachAdapter(this)
    }

    override val eventEmitter: Emitter = Emitter()

    var isPageShow = false

    private val mItemClick = { view: View ->
        val position = (view.tag as GalleryItemViewHolder).bindingAdapterPosition
        mData.item(position).apply {
            post("click", GalleryClick(view, toGallery()))
        }
        Unit
    }

    override fun getItemCount(): Int {
        return mData.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        return GalleryItemViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.bind(
                gid,
                token,
                detailThumb,
                title,
                uploader,
                Language.getLangSimple(language),
                category,
                timestamp.recordTime(),
                rating,
                if (isPageShow) pagesStr.toIntOrNull() ?: 0 else 0,
                "gallery:$gid$token"
            )
        }
    }
}