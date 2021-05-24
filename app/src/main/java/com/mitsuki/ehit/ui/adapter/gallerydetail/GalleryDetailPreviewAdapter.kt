package com.mitsuki.ehit.ui.adapter.gallerydetail

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.OriginalSize
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.extend.getInteger
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.ImageSource
import com.mitsuki.ehit.ui.widget.PreviewTransformation
import io.reactivex.rxjava3.subjects.PublishSubject

//详情adapter 05
class GalleryDetailPreviewAdapter :
    PagingDataAdapter<ImageSource, GalleryDetailPreviewAdapter.ViewHolder>(Diff.IMAGE_SOURCE) {

    private val mSubject: PublishSubject<ImageSource> by lazy { PublishSubject.create() }

    val event get() = mSubject.hideWithMainThread()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        mSubject.onNext(getItem(holder.bindingAdapterPosition))
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
                    crossfade(context.getInteger(R.integer.image_load_cross_fade))
                    if (it.left >= 0 && it.top >= 0 && it.right >= 0 && it.bottom >= 0) {
                        size(OriginalSize)
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