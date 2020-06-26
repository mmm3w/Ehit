package com.mitsuki.ehit.mvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.model.entity.Gallery
import com.mitsuki.ehit.mvvm.ui.widget.CategoryView
import java.util.*

class GalleryAdapter : PagingDataAdapter<Gallery, RecyclerView.ViewHolder>(Gallery.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        with(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        ) {
            return object : RecyclerView.ViewHolder(this) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.view<TextView>(R.id.galleryTitle)?.text = it.title
            holder.view<TextView>(R.id.galleryUploader)?.text = it.uploader
            holder.view<RatingView>(R.id.galleryRating)?.rating = it.rating ?: -1f
            holder.view<TextView>(R.id.galleryLang)?.text = it.languageSimple
            with(holder.view<CategoryView>(R.id.galleryCategory)) {
                this?.setCategoryColor(it.categoryColor)
                this?.text = it.category.toUpperCase(Locale.getDefault())
            }
            holder.view<TextView>(R.id.galleryTime)?.text = it.time
        }
    }
}