package com.mitsuki.ehit.ui.comment.adapter

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemCommentABinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.Comment

class GalleryCommentAdapter :
    RecyclerView.Adapter<GalleryCommentAdapter.ViewHolder>(), EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val mData: NotifyQueueData<Comment> = NotifyQueueData(Diff.GALLERY_COMMENT).apply {
        attachAdapter(this@GalleryCommentAdapter)
    }

    var isShowAll: Boolean = false
        set(value) {
            if (value == field) return

            when {
                !value && field -> notifyItemInserted(itemCount)
                value && !field -> notifyItemRemoved(itemCount - 1)
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryCommentAdapter.ViewHolder {
        return ViewHolder(parent).apply {
            binding.commentVoteUp.setOnClickListener {  }
            binding.commentVoteDown.setOnClickListener {  }
            binding.commentOption.setOnClickListener {  }
        }
    }

    override fun getItemCount(): Int = mData.count

    override fun onBindViewHolder(holder: GalleryCommentAdapter.ViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.binding.commentPostTime.text = time
            holder.binding.commentUserName.text = user
            holder.binding.commentContent.text =
                HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

            holder.binding.commentVoteUp.isSelected = true
        }
    }

    suspend fun submitData(data: List<Comment>) {
        when {
            data.isEmpty() && mData.count > 0 ->
                mData.postUpdate(NotifyData.Clear())
            data.isNotEmpty() && mData.count == 0 ->
                mData.postUpdate(NotifyData.RangeInsert(data))
            data.isEmpty() && mData.count == 0 -> {

            }
            else -> mData.postUpdate(NotifyData.Refresh(data))
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_comment_a)) {
        val binding by viewBinding(ItemCommentABinding::bind)
    }
}