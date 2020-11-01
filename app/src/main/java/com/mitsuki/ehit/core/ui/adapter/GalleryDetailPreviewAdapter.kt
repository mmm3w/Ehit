package com.mitsuki.ehit.core.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.size.OriginalSize
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.being.extend.getInteger
import com.mitsuki.ehit.being.load
import com.mitsuki.ehit.core.model.entity.ImageSource
import com.mitsuki.ehit.core.ui.widget.PreviewTransformation

//详情adapter 05
class GalleryDetailPreviewAdapter :
    PagingDataAdapter<ImageSource, RecyclerView.ViewHolder>(ImageSource.DIFF_CALLBACK) {

    val currentItem: MutableLiveData<ImageSource> = MutableLiveData()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        currentItem.postValue(getItem(holder.bindingAdapterPosition))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.view<TextView>(R.id.gallery_detail_preview_number)?.text = "${it.index + 1}"
            holder.view<ImageView>(R.id.gallery_detail_preview)?.apply {
                load(it.imageUrl) {
                    crossfade(context.getInteger(R.integer.image_load_cross_fade))
                    size(OriginalSize)
                    transformations(
                        PreviewTransformation(this@apply, it.left, it.top, it.right, it.bottom)
                    )
                    allowHardware(false)
                }
            }
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gallery_detail_preview, parent, false)
        )

}