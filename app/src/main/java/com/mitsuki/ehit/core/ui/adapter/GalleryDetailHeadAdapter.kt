package com.mitsuki.ehit.core.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate
import com.mitsuki.ehit.core.crutch.TagDiffer
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.ui.widget.CategoryView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryDetailHeadAdapter(private var mData: GalleryDetailWrap.DetailHeader) :
    RecyclerView.Adapter<GalleryDetailHeadAdapter.GalleryDetailHeadViewHolder>() {

    private val mGate = InitialGate()
    private var mEndOfPrepend = true
    private var mRefreshState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)

    fun setState(
        data: GalleryDetailWrap.DetailHeader? = null,
        refreshState: LoadState,
        endOfPrepend: Boolean
    ) {
        if (!mGate.ignore()) {
            if (refreshState != mRefreshState) {
                when (refreshState) {
                    is LoadState.Loading -> mGate.prep(true)
                    is LoadState.Error -> mGate.prep(false)
                    is LoadState.NotLoading -> mGate.trigger()
                }
                mRefreshState = refreshState
            }
        }
        if (endOfPrepend != mEndOfPrepend && mGate.ignore()) {
            if (mEndOfPrepend && !endOfPrepend) {
                notifyItemRemoved(0)
            } else if (endOfPrepend && !mEndOfPrepend) {
                notifyItemInserted(0)
            } else if (mEndOfPrepend && endOfPrepend) {
                notifyItemChanged(0)
            }
            data?.apply { mData = this }
            mEndOfPrepend = endOfPrepend
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryDetailHeadViewHolder {
        return GalleryDetailHeadViewHolder(parent)
    }

    override fun getItemCount(): Int {
        if (!mGate.ignore()) return 1
        return if (mEndOfPrepend) 1 else 0
    }

    override fun onBindViewHolder(holder: GalleryDetailHeadViewHolder, position: Int) {
        holder.bind(mData)
    }

    private fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
    }

    class GalleryDetailHeadViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_detail_header, parent, false)
    ) {
        private val mThumb = view<ImageView>(R.id.gallery_detail_thumb)
        private val mTitle = view<TextView>(R.id.gallery_detail_title)
        private val mUploader = view<TextView>(R.id.gallery_detail_uploader)
        private val mCategory = view<CategoryView>(R.id.gallery_detail_category)

        fun bind(data: GalleryDetailWrap.DetailHeader) {
            with(data) {
                mThumb?.apply {
                    transitionName = thumbTransitionName
                    load(data.thumb)
                }
                mTitle?.text = title
                mUploader?.text = uploader
                mCategory?.text = category
                mCategory?.setCategoryColor(categoryColor)
            }
        }
    }
}