package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate
import com.mitsuki.ehit.core.model.entity.Comment
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap

//详情adapter 04
class GalleryDetailCommentAdapter(private var mData: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailCommentAdapter.DetailCommentViewHolder>() {

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

                if (mGate.ignore()) notifyItemRangeInserted(0, mData.comment.size)

                field = loadState
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailCommentViewHolder {
        return DetailCommentViewHolder(parent, viewType)
    }

    override fun getItemCount(): Int = if (mGate.ignore()) mData.comment.size else 0

    override fun getItemViewType(position: Int): Int {
        if (position == mData.comment.size - 1) return R.layout.item_gallery_detail_comment
        return R.layout.item_comment
    }

    override fun onBindViewHolder(holder: DetailCommentViewHolder, position: Int) {
        if (position >= mData.comment.size - 1)
            holder.more(mData.comment[position].text)
        else
            holder.bind(mData.comment[position])
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