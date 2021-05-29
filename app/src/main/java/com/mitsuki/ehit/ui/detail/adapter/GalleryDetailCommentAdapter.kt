package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import io.reactivex.rxjava3.subjects.PublishSubject

class GalleryDetailCommentAdapter(private var mData: GalleryDetailWrap) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_COMMENT = 0
        private const val TYPE_MORE = 1
    }

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

    private val mSubject: PublishSubject<String> by lazy { PublishSubject.create() }

    val event get() = mSubject.hideWithMainThread()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return (when (viewType) {
            TYPE_COMMENT -> DetailCommentViewHolder(parent)
            TYPE_MORE -> MoreCommentViewHolder(parent)
            else -> throw  IllegalStateException()
        }).apply { itemView.setOnClickListener { mSubject.onNext("More Comment") } }
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
                    holder.postTime?.text = time
                    holder.userName?.text = user
                    holder.commentContent?.also {
                        it.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        it.maxLines = 5
                    }
                }
            }
            is MoreCommentViewHolder -> {
                holder.more?.apply {
                    text = when (mData.commentState) {
                        GalleryDetailWrap.CommentState.NoComments -> context.getText(R.string.text_no_comments)
                        GalleryDetailWrap.CommentState.AllLoaded -> context.getText(R.string.text_no_more_comments)
                        GalleryDetailWrap.CommentState.MoreComments -> context.getText(R.string.text_more_comments)
                    }
                }
            }
        }
    }

    class DetailCommentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_comment)) {
        val postTime = view<TextView>(R.id.comment_post_time)
        val userName = view<TextView>(R.id.comment_user_name)
        val commentContent = view<TextView>(R.id.comment_content)
    }

    class MoreCommentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_detail_comment)) {
        val more = view<TextView>(R.id.gallery_detail_more)
    }
}