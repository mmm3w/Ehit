package com.mitsuki.ehit.ui.download.adapter

import android.util.Log
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
import kotlin.math.roundToInt

class DownloadAdapter : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    companion object {
        const val PAYLOAD_PROGRESS_UPDATE = "PAYLOAD_PROGRESS_UPDATE"
    }

    private val mData: NotifyQueueData<DownloadListInfo> =
        NotifyQueueData(Diff.DOWNLOAD_LIST_INFO).apply {
            attachAdapter(this@DownloadAdapter)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mData.item(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                if (it is String && it == PAYLOAD_PROGRESS_UPDATE) {
                    holder.updateProgress(mData.item(position))
                } else {
                    super.onBindViewHolder(holder, position, payloads)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.count
    }

    class ViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.createItemView(R.layout.item_download)) {
        val binding by viewBinding(ItemDownloadBinding::bind)

        fun bind(info: DownloadListInfo) {
            with(info) {
                binding.downloadGalleryTitle.text = title
                binding.downloadProgressText.text = "${completed}/${total}"
                binding.downloadProgress.progress =
                    (completed.toFloat() / total.toFloat() * 100).roundToInt()
            }
        }

        fun updateProgress(info: DownloadListInfo) {
            with(info) {
                binding.downloadProgressText.text = "${completed}/${total}"
                binding.downloadProgress.progress =
                    (completed.toFloat() / total.toFloat() * 100).roundToInt()
            }
        }
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