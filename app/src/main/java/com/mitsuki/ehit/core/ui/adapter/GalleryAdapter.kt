package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.ui.widget.CategoryView
import java.util.*

class GalleryAdapter : PagingDataAdapter<Gallery, RecyclerView.ViewHolder>(Gallery.DIFF_CALLBACK) {

    val currentItem: MutableLiveData<Gallery> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        with(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        ) {
            return object : RecyclerView.ViewHolder(this) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.view<TextView>(R.id.gallery_title)?.text = it.title
            holder.view<TextView>(R.id.gallery_uploader)?.text = it.uploader
            holder.view<RatingView>(R.id.ggallery_rating)?.rating = it.rating
            holder.view<TextView>(R.id.gallery_lang)?.text = it.languageSimple
            with(holder.view<CategoryView>(R.id.gallery_category)) {
                this?.setCategoryColor(it.categoryColor)
                this?.text = it.category.toUpperCase(Locale.getDefault())
            }
            holder.view<TextView>(R.id.gallery_time)?.text = it.time
            holder.itemView.setOnClickListener { _ -> currentItem.postValue(it) }
        }
    }
}