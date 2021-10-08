package com.mitsuki.ehit.ui.detail.adapter

import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.base.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.crutch.event.Emitter
import com.mitsuki.ehit.crutch.event.EventEmitter
import com.mitsuki.ehit.crutch.event.post
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.ui.detail.layoutmanager.DetailOperatingLayoutManager
import io.reactivex.rxjava3.subjects.PublishSubject

//详情adapter 02
class GalleryDetailOperatingBlock(private val mData: GalleryDetailWrap) : SingleItemAdapter(false),
    EventEmitter {

    companion object {
        const val SCORE = "Score"
        const val SIMILARITYSEARCH = "SimilaritySearch"
        const val MOREINFO = "MoreInfo"
        const val DOWNLOAD = "Download"
        const val READ = "Read"
    }

    override val eventEmitter: Emitter = Emitter()

    override val layoutRes: Int = R.layout.item_gallery_detail_operating

    private val mOperatingAdapter by lazy { GalleryDetailOperatingPart(this) }
    private val mGate = InitialGate()

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (mGate.ignore()) return
            if (field != loadState) {
                when (loadState) {
                    is LoadState.Loading -> mGate.prep(true)
                    is LoadState.Error -> mGate.prep(false)
                    is LoadState.NotLoading -> mGate.trigger()
                }
                if (mGate.ignore()) isEnable = true
                field = loadState
            }
        }

    private var detailDownload: TextView? = null
    private var detailRead: TextView? = null
    private var detailPart: RecyclerView? = null

    override val onViewHolderCreate: ViewHolder.() -> Unit = {
        detailDownload = view<TextView>(R.id.gallery_detail_download)?.apply {
            setOnClickListener { post("operating", DOWNLOAD) }
        }
        detailRead = view<TextView>(R.id.gallery_detail_read)?.apply {
            setOnClickListener { post("operating", READ) }
        }
        detailPart = view<RecyclerView>(R.id.gallery_detail_part)?.apply {
            layoutManager = DetailOperatingLayoutManager(context)
            adapter = mOperatingAdapter
            addItemDecoration(mOperatingAdapter.divider)
        }
    }

    override val onViewHolderBind: ViewHolder.() -> Unit = {
        mOperatingAdapter.data = mData.partInfo
        mOperatingAdapter.notifyItemChanged(0)
    }

}