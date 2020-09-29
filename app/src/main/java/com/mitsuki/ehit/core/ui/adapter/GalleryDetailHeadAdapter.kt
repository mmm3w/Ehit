package com.mitsuki.ehit.core.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.core.ui.widget.CategoryView

class GalleryDetailHeadAdapter(private var mData: GalleryDetailWrap) :
    RecyclerView.Adapter<GalleryDetailHeadAdapter.GalleryDetailHeadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryDetailHeadViewHolder {
        return GalleryDetailHeadViewHolder(parent)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: GalleryDetailHeadViewHolder, position: Int) {
        holder.bind(mData.headInfo)
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
                mThumb?.load(data.thumb)
                mTitle?.text = title
                mUploader?.text = uploader
                mCategory?.text = category
                mCategory?.setCategoryColor(categoryColor)
            }
        }
    }
}