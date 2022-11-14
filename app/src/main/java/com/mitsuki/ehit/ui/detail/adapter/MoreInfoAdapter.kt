package com.mitsuki.ehit.ui.detail.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extensions.copying2Clipboard
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.crutch.network.site.ApiContainer
import com.mitsuki.ehit.model.entity.GalleryDetail

class MoreInfoAdapter(info: GalleryDetail) : RecyclerView.Adapter<MoreInfoAdapter.ViewHolder>() {

    private val mInfo: List<Pair<String, String>> = transInfo(info)

    private val mItemLongClick = { view: View ->
        val position = (view.tag as ViewHolder).bindingAdapterPosition
        mInfo[position].second.copying2Clipboard()
        AppHolder.toast(textRes = R.string.hint_copy_successfully)
        true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {
            itemView.tag = this
            itemView.setOnLongClickListener(mItemLongClick)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(mInfo[position]) {
            holder.keyView?.text = first
            holder.valueView?.text = second
        }
    }

    override fun getItemCount(): Int = mInfo.size

    private fun transInfo(info: GalleryDetail): List<Pair<String, String>> {
        return arrayListOf(
            string(R.string.text_info_gid) to info.gid.toString(),
            string(R.string.text_info_token) to info.token,
            string(R.string.text_info_url) to ApiContainer.galleryDetail(info.gid, info.token),
            string(R.string.text_info_title) to info.title,
            string(R.string.text_info_title_jp) to info.titleJP,
            string(R.string.text_info_thumb) to info.detailThumb,
            string(R.string.text_info_category) to info.category,
            string(R.string.text_info_uploader) to info.uploader,
            string(R.string.text_info_posted_time) to info.posted,
            string(R.string.text_info_visible) to info.visible,
            string(R.string.text_info_language) to info.language,
            string(R.string.text_info_pages) to info.pages.toString(),
            string(R.string.text_info_size) to info.size,
            string(R.string.text_info_favorite_count) to info.favoriteCount.toString(),
            string(R.string.text_info_favorited) to info.isFavorited.toString(),
            string(R.string.text_info_rating_count) to info.ratingCount.toString(),
            string(R.string.text_info_rating) to info.rating.toString(),
            string(R.string.text_info_torrents) to info.torrentCount.toString(),
        )
    }


    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_more_info)) {
        val keyView = view<TextView>(R.id.more_info_key)
        val valueView = view<TextView>(R.id.more_info_value)
    }
}