@file:Suppress("UNUSED_PARAMETER")

package com.mitsuki.ehit.core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.view
import com.mitsuki.armory.widget.TagsView
import com.mitsuki.ehit.R
import com.mitsuki.ehit.core.model.entity.*
import com.mitsuki.ehit.core.ui.layoutmanager.DetailOperatingLayoutManager
import com.mitsuki.ehit.core.ui.widget.CategoryView
import java.util.*

class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)


class DetailHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailTitle = view.findViewById<TextView>(R.id.galleryDetailTitle)
    private val detailUploader = view.findViewById<TextView>(R.id.galleryDetailUploader)
    private val detailCategory = view.findViewById<CategoryView>(R.id.galleryDetailCategory)
    fun binds(holder: DetailHeaderViewHolder, position: Int, item: DetailHeader) {
        detailTitle?.text = item.title
        detailUploader?.text = item.uploader
        detailCategory?.run {
            setCategoryColor(item.categoryColor)
            text = item.category.toUpperCase(Locale.getDefault())
        }


        //上传者点击事件监听
    }
}

class DetailPartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailPart = view.findViewById<RecyclerView>(R.id.galleryDetailPart)
    fun binds(part: DetailPart, ad: PartAdapter) {
        //整块view点击监听
        ad.part = part
        detailPart?.run {
            layoutManager = DetailOperatingLayoutManager(context)
            adapter = ad
            addItemDecoration(ad.divider)
        }
    }
}

class DetailOperatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailDownload = view.findViewById<TextView>(R.id.galleryDetailDownload)
    private val detailRead = view.findViewById<TextView>(R.id.galleryDetailRead)
    fun binds(holder: DetailOperatingViewHolder, position: Int, item: DetailOperating) {
        //两个按钮点击事件监听
    }
}

@Suppress("DEPRECATION")
class DetailTagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailTags = view.findViewById<TagsView>(R.id.galleryDetailTag)
    fun binds(holder: DetailTagViewHolder, position: Int, item: DetailTag) {
        detailTags?.run {
            removeAllViews()
            LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_gallery_detail_tag_title, this, false)
                .run {
                    (this as TextView).text = item.tagSet.setName
                    addView(this)
                }
            for (tag in item.tagSet.tags) {
                LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_gallery_detail_tag_item, this, false)
                    .run {
                        (this as TextView).text = tag
                        addView(this)
                    }
            }
        }
    }
}

class DetailCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailComments = view.findViewById<LinearLayout>(R.id.galleryDetailComments)
    fun binds(holder: DetailCommentViewHolder, position: Int, item: DetailComment) {
        detailComments.let {
            it.removeViews(0, it.childCount - 1)
            if (item.comments.size < 1) {
                holder.view<TextView>(R.id.galleryDetailMore)?.text = "暂无评论"
            } else {
                val inflater = LayoutInflater.from(holder.itemView.context)
                for ((count, comment) in item.comments.withIndex()) {
                    val commentViewGroup = inflater.inflate(R.layout.item_comment, it, false)
                    commentViewGroup.findViewById<TextView>(R.id.postTime).text = comment.time
                    commentViewGroup.findViewById<TextView>(R.id.userName).text = comment.user
                    commentViewGroup.findViewById<TextView>(R.id.commentContent)?.run {
                        text = HtmlCompat.fromHtml(comment.text, FROM_HTML_MODE_LEGACY)
                        maxLines = 5
                    }
                    it.addView(commentViewGroup, count)
                }
                holder.view<TextView>(R.id.galleryDetailMore)?.text =
                    if (item.isAll) "已显示全部评论" else "更多评论"
            }
        }

        //整块的view都需要点击事件监听
    }
}

class DetailPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val detailPreview: ImageView = view.findViewById<ImageView>(R.id.galleryDetailPreview)
    private val detailPage = view.findViewById<TextView>(R.id.galleryDetailPreviewNumber)
    fun binds(holder: RecyclerView.ViewHolder, position: Int, item: DetailPreview) {
        detailPage.text = item.page.toString()
    }
}