package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.view
import com.mitsuki.armory.widget.TagsView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.TagDiffer
import com.mitsuki.ehit.core.model.entity.TagSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//详情adapter 03
class GalleryDetailTagAdapter(private var mData: Array<TagSet>) :
    RecyclerView.Adapter<GalleryDetailTagAdapter.DetailTagViewHolder>() {

    //顶部加载完成tag
    private var mEndOfPrepend = true

    suspend fun setData(data: Array<TagSet>, endOfPrepend: Boolean) {
        if (endOfPrepend != mEndOfPrepend) {
            if (mEndOfPrepend && !endOfPrepend) {
                notifyItemRangeRemoved(0, mData.size)
            } else if (endOfPrepend && !mEndOfPrepend) {
                notifyItemRangeInserted(0, data.size)
            } else if (mEndOfPrepend && endOfPrepend) {
                withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(TagDiffer(mData, data))
                }.dispatchUpdatesTo(this)
            }
            mData = data
            mEndOfPrepend = endOfPrepend
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        return DetailTagViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (mEndOfPrepend) mData.size else 0
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        holder.bind(mData[position], position == (mData.size - 1))
    }

    class DetailTagViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_detail_tag, parent, false)
    ) {
        private val detailTags = view<TagsView>(R.id.gallery_detail_tag)

        fun bind(data: TagSet, isLast: Boolean) {
            detailTags?.run {
                removeAllViews()
                LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_gallery_detail_tag_title, this, false)
                    .run {
                        (this as TextView).text = data.setName
                        addView(this)
                    }
                for (tag in data.tags) {
                    LayoutInflater.from(itemView.context)
                        .inflate(R.layout.item_gallery_detail_tag_item, this, false)
                        .run {
                            (this as TextView).text = tag
                            addView(this)
                        }
                }
                setPadding(0, 0, 0, if (isLast) dp2px(16f) else 0)

                //还要发射点击事件
            }
        }
    }
}