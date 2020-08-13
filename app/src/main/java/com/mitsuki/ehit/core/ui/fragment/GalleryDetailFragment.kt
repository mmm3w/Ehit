package com.mitsuki.ehit.core.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mitsuki.ehit.R
import com.mitsuki.ehit.base.BaseFragment
import com.mitsuki.ehit.core.ui.adapter.GalleryDetailAdapter
import com.mitsuki.ehit.core.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_gallery_detail.*

class GalleryDetailFragment : BaseFragment(R.layout.fragment_gallery_detail) {

    private val mViewModel: MainViewModel by activityViewModels()
    private val mAdapter by lazy { GalleryDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.galleryDetail()?.observe(this@GalleryDetailFragment, Observer {
            mAdapter.submitData(lifecycle, it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        galleryDetailList.adapter = mAdapter
        galleryDetailList.layoutManager =
            GridLayoutManager(activity, 3).apply { spanSizeLookup = mAdapter.mSpanSizeLookup }
    }
}