package com.mitsuki.ehit.ui.comment.adapter

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(parent).apply {
            binding.commentVoteUp.setOnClickListener { }
            binding.commentVoteDown.setOnClickListener { }
            binding.commentOption.setOnClickListener { }
        }
    }

    override fun getItemCount(): Int = mData.count

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.binding.commentUserName.text = user
            holder.binding.commentPostTime.text = postTime.toString()
            holder.binding.commentContent.text =
                HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
            holder.binding.commentVoteLayout.isVisible = voteEnable
            holder.binding.commentVoteUp.isSelected = voteState > 0
            holder.binding.commentVoteUp.text = score
            holder.binding.commentVoteDown.isSelected = voteState < 0
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