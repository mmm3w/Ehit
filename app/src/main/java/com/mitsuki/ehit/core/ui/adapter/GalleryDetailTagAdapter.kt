package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.view
import com.mitsuki.armory.widget.TagsView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.model.entity.TagSet

//详情adapter 03
class GalleryDetailTagAdapter(private val mData: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailTagAdapter.DetailTagViewHolder>() {

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

                if (mGate.ignore()) notifyItemRangeInserted(0, mData.tags.size)

                field = loadState
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTagViewHolder {
        return DetailTagViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return if (mGate.ignore()) mData.tags.size else 0
    }

    override fun onBindViewHolder(holder: DetailTagViewHolder, position: Int) {
        holder.bind(mData.tags[position], position == (mData.tags.size - 1))
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