package com.mitsuki.ehit.ui.download.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.notify.NotifyData
import com.mitsuki.armory.adapter.notify.coroutine.NotifyQueueData
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extensions.createItemView
import com.mitsuki.ehit.crutch.extensions.viewBinding
import com.mitsuki.ehit.databinding.ItemDownloadBinding
import com.mitsuki.ehit.model.diff.Diff
import com.mitsuki.ehit.model.entity.DownloadListInfo
import com.mitsuki.ehit.model.entity.db.DownloadBaseInfo
import com.mitsuki.ehit.model.entity.db.SearchHistory
import kotlin.math.roundToInt

class DownloadAdapter : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    private val mData: NotifyQueueData<DownloadListInfo> =
        NotifyQueueData(Diff.DOWNLOAD_LIST_INFO).apply {
            attachAdapter(this@DownloadAdapter)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(mData.item(position)) {
            holder.binding.downloadGalleryTitle.text = title
            holder.binding.downloadProgressText.text = "${completed}/${total}"
            holder.binding.downloadProgress.progress =
                (completed.toFloat() / total.toFloat() * 100).roundToInt()
        }
    }

    override fun getItemCount(): Int {
        return mData.count
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_download)) {
        val binding by viewBinding(ItemDownloadBinding::bind)
    }

    suspend fun submitData(data: List<DownloadListInfo>) {
        when {
            data.isEmpty() && mData.count > 0 ->
                mData.postUpdate(NotifyData.Clear())
            data.isNotEmpty() && mData.count == 0 ->
                mData.postUpdate(NotifyData.RangeInsert(data))
            data.isEmpty() && mData.count == 0 -> {

            }
            else -> mData.postUpdate(NotifyData.Refresh(data))
        }
    }
}