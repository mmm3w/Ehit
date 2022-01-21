package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.Tools
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extend.text
import com.mitsuki.ehit.crutch.extend.viewBinding
import com.mitsuki.ehit.databinding.ItemCommentBinding
import com.mitsuki.ehit.databinding.ItemGalleryDetailCommentBinding
import com.mitsuki.ehit.model.entity.GalleryDetailWrap

class GalleryDetailCommentAdapter(private var mData: GalleryDetailWrap) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), EventEmitter {

    companion object {
        private const val TYPE_COMMENT = 0
        private const val TYPE_MORE = 1
    }

    override val eventEmitter: Emitter = Emitter()

    private val mGate = InitialGate()

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (mGate.ignore()) return

            if (field != loadState) {
                when (loadState) {
                    is LoadState.Loading -> mGate.prep(true)
                    is LoadState.Error -> mGate.prep(false)
                    is LoadState.NotLoading -> mGate.trigger()
                }

                if (mGate.ignore()) notifyItemRangeInserted(0, mData.comment.size + 1)

                field = loadState
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return (when (viewType) {
            TYPE_COMMENT -> DetailCommentViewHolder(parent)
            TYPE_MORE -> MoreCommentViewHolder(parent)
            else -> throw  IllegalStateException()
        }).apply { itemView.setOnClickListener { post("comment", "More Comment") } }
    }

    override fun getItemCount(): Int = if (mGate.ignore()) mData.comment.size + 1 else 0

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1)
            return TYPE_MORE
        return TYPE_COMMENT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailCommentViewHolder -> {
                with(mData.comment[position]) {
                    holder.binding.commentPostTime.text = Tools.commentTime(postTime)
                    holder.binding.commentUserName.text = user
                    holder.binding.commentContent.also {
                        it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        it.maxLines = 3
                    }
                }
            }
            is MoreCommentViewHolder -> {
                holder.binding.galleryDetailMore.text = when (mData.commentState) {
                    GalleryDetailWrap.CommentState.NoComments -> text(R.string.text_no_comments)
                    GalleryDetailWrap.CommentState.AllLoaded -> text(R.string.text_no_more_comments)
                    GalleryDetailWrap.CommentState.MoreComments -> text(R.string.text_more_comments)
                }
            }
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