package com.mitsuki.ehit.mvvm.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.armory.widget.RatingView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.mvvm.model.entity.*
import com.mitsuki.ehit.mvvm.ui.widget.CategoryView
import java.util.*

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)


class DetailHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun binds(holder: DetailHeaderViewHolder, position: Int, item: DetailHeader) {
        holder.view<TextView>(R.id.galleryDetailTitle)?.text = item.title
        holder.view<TextView>(R.id.galleryDetailUploader)?.text = item.uploader
        with(holder.view<CategoryView>(R.id.galleryCategory)) {
            this?.setCategoryColor(item.categoryColor)
            this?.text = item.category.toUpperCase(Locale.getDefault())
        }
    }
}

class DetailPartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    @SuppressLint("SetTextI18n")
    fun binds(holder: DetailPartViewHolder, position: Int, item: DetailPart) {
        holder.view<TextView>(R.id.galleryDetailLang)?.text = item.lang
        holder.view<TextView>(R.id.galleryDetailPage)?.text = item.page.toString()
        holder.view<TextView>(R.id.galleryDetailSize)?.text = item.size

        holder.view<TextView>(R.id.galleryDetailFavoredTimes)?.text = "❤${item.favCount}"
        holder.view<TextView>(R.id.galleryDetailPosted)?.text = item.posted
    }
}

class DetailOperatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun binds(holder: DetailOperatingViewHolder, position: Int, item: DetailOperating) {
        holder.view<TextView>(R.id.galleryDetailRatingText)?.text = item.summary
        holder.view<RatingView>(R.id.galleryDetailRating)?.rating = item.rating
        holder.view<RecyclerView>(R.id.galleryDetailOperating)?.let {
            it.layoutManager = LinearLayoutManager(holder.itemView.context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            it.adapter = OperatingAdapter
        }
    }
}

class DetailTagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun binds(holder: DetailTagViewHolder, position: Int, item: DetailTag) {
        holder.view<TextView>(R.id.galleryDetailTag)?.text = item.tagSet.setName
    }
}

class DetailCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun binds(holder: DetailCommentViewHolder, position: Int, item: DetailComment) {

    }
}

class DetailPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun binds(holder: RecyclerView.ViewHolder, position: Int, item: DetailPreview) {

    }
}