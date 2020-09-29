package com.mitsuki.ehit.core.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.mitsuki.armory.extend.view
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.Gallery
import com.mitsuki.ehit.core.ui.widget.CategoryView
import java.util.*

class GalleryAdapter :
    PagingDataAdapter<Gallery, GalleryAdapter.ViewHolder>(Gallery.DIFF_CALLBACK) {

    val currentItem: MutableLiveData<GalleryClick> = MutableLiveData()

    private val mItemClick = { view: View ->
        val holder = view.tag as ViewHolder
        getItem(holder.bindingAdapterPosition).diffuse {
            currentItem.postValue(
                currentItem.value?.copy(target = view, data = this)
                    ?: GalleryClick(target = view, data = this)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnClickListener(mItemClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.view<TextView>(R.id.gallery_title)?.text = it.title
            holder.view<TextView>(R.id.gallery_uploader)?.text = it.uploader
            holder.view<RatingView>(R.id.ggallery_rating)?.rating = it.rating
            holder.view<TextView>(R.id.gallery_lang)?.text = it.languageSimple
            holder.view<CategoryView>(R.id.gallery_category)?.apply {
                setCategoryColor(it.categoryColor)
                text = it.category.toUpperCase(Locale.getDefault())
            }
            holder.view<TextView>(R.id.gallery_time)?.text = it.time
//            holder.view<ImageView>(R.id.gallery_thumb)?.load(it.thumb) { crossfade(300) }
            holder.view<CardView>(R.id.gallery_card)?.transitionName = it.itemTransitionName
        }
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        ) {
    }

    private fun Gallery?.diffuse(action: Gallery.() -> Unit) {
        this?.apply(action)
    }

    data class GalleryClick(val target: View, val data: Gallery)
}