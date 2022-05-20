package com.mitsuki.ehit.ui.detail.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load

import coil.size.Size
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.coil.CacheKey
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.ui.common.widget.PreviewTransformation

//详情adapter 05
class GalleryDetailPreviewAdapter(
    private val gid: Long,
    private val token: String
) : PagingDataAdapter<ImageSource, GalleryDetailPreviewAdapter.ViewHolder>(Diff.IMAGE_SOURCE),
    EventEmitter {

    override val eventEmitter: Emitter = Emitter()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        post("detail", peek(holder.bindingAdapterPosition))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.numberView?.text = "${it.index + 1}"
            holder.previewView?.apply {
                load(it.imageUrl) {
                    val metaTag = CacheKey.previewKey(gid, token, it.index + 1)
                    placeholderMemoryCacheKey(CacheKey.largeTempKey(metaTag))
                    if (it.left >= 0 && it.top >= 0 && it.right >= 0 && it.bottom >= 0) {
                        size(Size.ORIGINAL)
                        transformations(
                            PreviewTransformation(this@apply, it.left, it.top, it.right, it.bottom)
                        )
                    }
                }
            }
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_gallery_detail_preview)) {
        val previewView = view<ImageView>(R.id.gallery_detail_preview)
        val numberView = view<TextView>(R.id.gallery_detail_preview_number)
    }

}