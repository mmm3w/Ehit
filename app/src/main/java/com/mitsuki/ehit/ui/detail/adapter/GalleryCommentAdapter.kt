package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.dynamic.IFragmentWrapper
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.text
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemCommentBinding
import com.mitsuki.ehit.databinding.ItemGalleryBinding
import com.mitsuki.ehit.databinding.ItemGalleryDetailCommentBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.Comment
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import io.reactivex.rxjava3.subjects.PublishSubject

class GalleryCommentAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), EventEmitter {

    companion object {
        private const val TYPE_COMMENT = 0
        private const val TYPE_MORE = 1
    }

    override val eventEmitter: Emitter = Emitter()

    private val mData: NotifyQueueData<Comment> = NotifyQueueData(Diff.GALLERY_COMMENT).apply {
        attachAdapter(this@GalleryCommentAdapter)
    }

    var isShowAll: Boolean = true
        set(value) {
            if (value == field) return

            when {
                !value && field -> notifyItemInserted(itemCount)
                value && !field -> notifyItemRemoved(itemCount - 1)
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COMMENT -> DetailCommentViewHolder(parent).apply {

            }
            TYPE_MORE -> MoreCommentViewHolder(parent).apply {
                itemView.setOnClickListener { post("more", 0) }
            }
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = if (isShowAll) mData.count else mData.count + 1

    override fun getItemViewType(position: Int): Int {
        if (!isShowAll && position == itemCount - 1)
            return TYPE_MORE
        return TYPE_COMMENT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailCommentViewHolder -> {
                with(mData.item(position)) {
                    holder.binding.commentPostTime.text = time
                    holder.binding.commentUserName.text = user
                    holder.binding.commentContent.text =
                        HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }
            is MoreCommentViewHolder -> {
                holder.binding.galleryDetailMore.text = text(R.string.text_more_comments)
            }
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

    class DetailCommentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_comment)) {
        val binding by viewBinding(ItemCommentBinding::bind)
    }

    class MoreCommentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_detail_comment)) {
        val binding by viewBinding(ItemGalleryDetailCommentBinding::bind)
    }
}