package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.CommentDiffer
import com.mitsuki.ehit.core.crutch.TagDiffer
import com.mitsuki.ehit.core.model.entity.Comment
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.TagSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//详情adapter 04
class GalleryDetailCommentAdapter(private var mData: Array<Comment>) :
    RecyclerView.Adapter<GalleryDetailCommentAdapter.DetailCommentViewHolder>() {

    private var mEndOfPrepend = true

    suspend fun setData(data: Array<Comment>, endOfPrepend: Boolean) {
        if (endOfPrepend != mEndOfPrepend) {
            if (mEndOfPrepend && !endOfPrepend) {
                notifyItemRangeRemoved(0, mData.size)
            } else if (endOfPrepend && !mEndOfPrepend) {
                notifyItemRangeInserted(0, data.size)
            } else if (mEndOfPrepend && endOfPrepend) {
                withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(CommentDiffer(mData, data))
                }.dispatchUpdatesTo(this)
            }
            mData = data
            mEndOfPrepend = endOfPrepend
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailCommentViewHolder {
        return DetailCommentViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int = if (mEndOfPrepend) mData.size else 0

    override fun getItemViewType(position: Int): Int {
        if (position == mData.size - 1) return R.layout.item_gallery_detail_comment
        return R.layout.item_comment
    }

    override fun onBindViewHolder(holder: DetailCommentViewHolder, position: Int) {
        if (position >= mData.size - 1)
            holder.more(mData[position].text)
        else
            holder.bind(mData[position])
    }

    class DetailCommentViewHolder(parent: ViewGroup, layout: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        ) {

        private val mMore = view<TextView>(R.id.gallery_detail_more)

        private val mPostTime = view<TextView>(R.id.comment_post_time)
        private val mUserName = view<TextView>(R.id.comment_user_name)
        private val mCommentContent = view<TextView>(R.id.comment_content)

        fun bind(item: Comment) {
            mPostTime?.text = item.time
            mUserName?.text = item.user
            mCommentContent?.run {
                text = HtmlCompat.fromHtml(item.text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                maxLines = 5
            }
        }

        fun more(text: String) {
            mMore?.text = text
        }
    }

}