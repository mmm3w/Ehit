package com.mitsuki.ehit.ui.detail.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.dp2px
import com.mitsuki.armory.extend.view
import com.mitsuki.armory.widget.TagsView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.SingleLiveEvent
import com.mitsuki.ehit.model.entity.GalleryDetailWrap

//详情adapter 03
class GalleryDetailTagAdapter(private val mData: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailTagAdapter.DetailTagViewHolder>() {

    val tagEvent: SingleLiveEvent<Pair<String, String>> by lazy { SingleLiveEvent() }

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
        with(mData.tags[position]) {
            holder.detailTags?.apply {
                removeAllViews()
                createItemView(R.layout.item_gallery_detail_tag_title).run {
                    (this as TextView).text = groupName
                    addView(this)
                }
                for (tag in tags) {
                    createItemView(R.layout.item_gallery_detail_tag_item).run {
                        (this as TextView).text = tag
                        setOnClickListener { tagEvent.postValue(groupName to tag) }
                        addView(this)
                    }
                }
                setPadding(0, 0, 0, if (position == (mData.tags.size - 1)) dp2px(16f).toInt() else 0)
            }
        }
    }

    class DetailTagViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_detail_tag)) {
        val detailTags = view<TagsView>(R.id.gallery_detail_tag)
    }
}