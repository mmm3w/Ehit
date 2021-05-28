package com.mitsuki.ehit.ui.gallerydetail.adapter

import android.widget.TextView
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.adapter.SingleItemAdapter
import com.mitsuki.armory.extend.view
import com.mitsuki.ehit.R
import com.mitsuki.ehit.crutch.extend.hideWithMainThread
import com.mitsuki.ehit.crutch.InitialGate
import com.mitsuki.ehit.model.entity.GalleryDetailWrap
import com.mitsuki.ehit.ui.gallerydetail.layoutmanager.DetailOperatingLayoutManager
import io.reactivex.rxjava3.subjects.PublishSubject

//详情adapter 02
class GalleryDetailOperatingBlock(private val mData: GalleryDetailWrap) : SingleItemAdapter(false) {
    override val layoutRes: Int = R.layout.item_gallery_detail_operating

    private val mSubject: PublishSubject<Event> by lazy { PublishSubject.create() }
    val event get() = mSubject.hideWithMainThread()

    private val mOperatingAdapter by lazy { GalleryDetailOperatingPart(mSubject) }
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
            setOnClickListener { mSubject.onNext(Event.Download) }
        }
        detailRead = view<TextView>(R.id.gallery_detail_read)?.apply {
            setOnClickListener { mSubject.onNext(Event.Read) }
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

    sealed class Event {
        object Score : Event()
        object SimilaritySearch : Event()
        object MoreInfo : Event()
        object Download : Event()
        object Read : Event()
    }

}