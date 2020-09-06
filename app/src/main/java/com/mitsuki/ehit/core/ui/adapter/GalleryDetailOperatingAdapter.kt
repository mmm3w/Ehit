package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.ui.layoutmanager.DetailOperatingLayoutManager

//详情adapter 02
class GalleryDetailOperatingAdapter(val data: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailOperatingAdapter.DetailOperatingViewHolder>() {

    private val mOperatingAdapter by lazy { PartAdapter(data.partInfo) }

    var endOfPrepend: Boolean = true
        set(endOfPrepend) {
            if (field != endOfPrepend) {
                if (field && !endOfPrepend) {
                    notifyItemRemoved(0)
                } else if (endOfPrepend && !field) {
                    notifyItemInserted(0)
                } else if (field && endOfPrepend) {
                    notifyItemChanged(0)
                }
                field = endOfPrepend
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailOperatingViewHolder {
        return DetailOperatingViewHolder(parent, mOperatingAdapter)
    }

    override fun getItemCount(): Int = if (endOfPrepend) 1 else 0

    override fun onBindViewHolder(holder: DetailOperatingViewHolder, position: Int) {
        mOperatingAdapter.data = data.partInfo
        mOperatingAdapter.notifyItemRangeChanged(0, 1)
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