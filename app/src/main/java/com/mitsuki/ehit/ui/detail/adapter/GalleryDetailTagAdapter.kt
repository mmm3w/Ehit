package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.armory.base.extend.dp2px
import com.mitsuki.armory.base.extend.view
import com.mitsuki.armory.base.widget.TagsView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.model.entity.TagGroup
import com.mitsuki.ehit.crutch.event.post

class GalleryDetailTagAdapter(private val mData: NotifyQueueData<TagGroup>) :
    RecyclerView.Adapter<GalleryDetailTagAdapter.DetailTagViewHolder>(), EventEmitter {

    init {
        mData.attachAdapter(this)
    }

    override val eventEmitter: Emitter = Emitter()

    override fun getItemCount(): Int = mData.count

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        return DetailTagViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.detailTags?.apply {
                removeAllViews()
                createItemView(R.layout.item_gallery_detail_tag_title).run {
                    (this as TextView).text = groupName
                    addView(this)
                }
                for (tag in tags) {
                    createItemView(R.layout.item_gallery_detail_tag_item).run {
                        (this as TextView).text = tag
                        setOnClickListener { post("tag", groupName to tag) }
                        addView(this)
                    }
                }
                setPadding(
                    0,
                    0,
                    0,
                    if (position == (mData.count - 1)) dp2px(16f).toInt() else 0
                )
            }
        }
    }

    class DetailTagViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_detail_tag)) {
        val detailTags = view<TagsView>(R.id.gallery_detail_tag)
    }
}