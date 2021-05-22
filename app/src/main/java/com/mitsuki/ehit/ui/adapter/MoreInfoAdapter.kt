package com.mitsuki.ehit.ui.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.AppHolder
import com.mitsuki.ehit.crutch.extend.createItemView
import com.mitsuki.ehit.crutch.network.Url
import com.mitsuki.ehit.model.entity.GalleryDetail

class MoreInfoAdapter(info: GalleryDetail) : RecyclerView.Adapter<MoreInfoAdapter.ViewHolder>() {

    private val mInfo: List<Pair<String, String>> = transInfo(info)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent).apply {

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
//            AppHolder.string(R.string.text_info_gid) to info.gid.toString(),
//            AppHolder.string(R.string.text_info_token) to info.token,
//            AppHolder.string(R.string.text_info_url) to Url.galleryDetail(info.gid, info.token),
//            AppHolder.string(R.string.text_info_title) to info.title,
//            AppHolder.string(R.string.text_info_title_jp) to info.titleJP,
//            AppHolder.string(R.string.text_info_thumb) to info.detailThumb,
//            AppHolder.string(R.string.text_info_category) to info.category,
//            AppHolder.string(R.string.text_info_uploader) to info.uploader,
//            AppHolder.string(R.string.text_info_posted_time) to info.posted,
//            AppHolder.string(R.string.text_info_visible) to info.visible,
//            AppHolder.string(R.string.text_info_language) to info.language,
//            AppHolder.string(R.string.text_info_pages) to info.pages.toString(),
//            AppHolder.string(R.string.text_info_size) to info.size,
//            AppHolder.string(R.string.text_info_favorite_count) to info.favoriteCount.toString(),
//            AppHolder.string(R.string.text_info_favorited) to info.isFavorited.toString(),
//            AppHolder.string(R.string.text_info_rating_count) to info.ratingCount.toString(),
//            AppHolder.string(R.string.text_info_rating) to info.rating.toString(),
//            AppHolder.string(R.string.text_info_torrents) to info.torrentCount.toString(),
        )
    }


    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_more_info)) {
        val keyView = view<TextView>(R.id.more_info_key)
        val valueView = view<TextView>(R.id.more_info_value)
    }
}