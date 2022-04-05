package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.uils.TimeFormat
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemCommentBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.Comment

class GalleryDetailCommentAdapter :
    RecyclerView.Adapter<GalleryDetailCommentAdapter.DetailCommentViewHolder>(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val mData: NotifyQueueData<Comment> by lazy {
        NotifyQueueData(Diff.GALLERY_COMMENT).apply {
            attachAdapter(this@GalleryDetailCommentAdapter)
        }
    }

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

    fun postData(data: List<Comment>) {
        if (data.isEmpty()) {
            mData.postUpdate(NotifyData.Clear())
        } else {
            mData.postUpdate(NotifyData.Refresh(data))
        }
    }

    class DetailCommentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_comment)) {
        val binding by viewBinding(ItemCommentBinding::bind)

    }
}