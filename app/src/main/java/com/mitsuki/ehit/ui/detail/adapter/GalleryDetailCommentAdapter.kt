package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.uils.TimeFormat
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.databinding.ItemCommentBinding
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.ui.common.adapter.BindingViewHolder

class GalleryDetailCommentAdapter(private val mData: NotifyQueueData<Comment>) :
    RecyclerView.Adapter<GalleryDetailCommentAdapter.DetailCommentViewHolder>(), EventEmitter {
    init {
        mData.attachAdapter(this)
    }

    override val eventEmitter: Emitter = Emitter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailCommentViewHolder {
        return DetailCommentViewHolder(parent).apply {
            itemView.setOnClickListener {
                post("comment", "More Comment")
            }
        }
    }

    override fun getItemCount(): Int = mData.count

    override fun onBindViewHolder(holder: DetailCommentViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.binding.commentPostTime.text = TimeFormat.commentTime(postTime)
            holder.binding.commentUserName.text = user
            holder.binding.commentContent.also {
                it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                it.maxLines = 3
            }
        }
    }

    class DetailCommentViewHolder(parent: ViewGroup) :
        BindingViewHolder<ItemCommentBinding>(
            parent,
            R.layout.item_comment,
            ItemCommentBinding::bind
        )
}