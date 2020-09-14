package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.crutch.InitialGate
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.ui.layoutmanager.DetailOperatingLayoutManager

//详情adapter 02
class GalleryDetailOperatingAdapter(private val mData: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailOperatingAdapter.DetailOperatingViewHolder>() {

    private val mOperatingAdapter by lazy { PartAdapter() }

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

                if (mGate.ignore()) notifyItemInserted(0)

                field = loadState
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailOperatingViewHolder {
        return DetailOperatingViewHolder(parent, mOperatingAdapter)
    }

    override fun getItemCount(): Int = if (mGate.ignore()) 1 else 0

    override fun onBindViewHolder(holder: DetailOperatingViewHolder, position: Int) {
        mOperatingAdapter.data = mData.partInfo
        mOperatingAdapter.notifyItemChanged(0)
    }

    class DetailOperatingViewHolder(parent: ViewGroup, partAdapter: PartAdapter) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gallery_detail_operating, parent, false)
        ) {
        private val detailDownload = view<TextView>(R.id.gallery_detail_download)
        private val detailRead = view<TextView>(R.id.gallery_detail_read)
        private val detailPart = view<RecyclerView>(R.id.gallery_detail_part)?.apply {
            layoutManager = DetailOperatingLayoutManager(context)
            adapter = partAdapter
            addItemDecoration(partAdapter.divider)
        }
    }


}