package com.mitsuki.ehit.core.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.OriginalSize
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.ui.widget.PreviewTransformation

//详情adapter 05
class GalleryDetailPreviewAdapter :
    PagingDataAdapter<ImageSource, RecyclerView.ViewHolder>(ImageSource.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        with(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gallery_detail_preview, parent, false)
        ) {
            return object : RecyclerView.ViewHolder(this) {}
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.view<TextView>(R.id.gallery_detail_preview_number)?.text = "${position + 1}"
            holder.view<ImageView>(R.id.gallery_detail_preview)?.apply {
                load(it.url) {
                    crossfade(200)
                    size(OriginalSize)
                    transformations(
                        PreviewTransformation(this@apply, it.left, it.top, it.right, it.bottom)
                    )
                }
            }
        }
    }


}